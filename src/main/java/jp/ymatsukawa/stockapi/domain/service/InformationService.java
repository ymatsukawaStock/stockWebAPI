package jp.ymatsukawa.stockapi.domain.service;

import java.util.*;
import java.util.stream.Collectors;

import jp.ymatsukawa.stockapi.domain.entity.bridge.BridgeInformation;
import jp.ymatsukawa.stockapi.domain.entity.bridge.BridgeInformationTags;
import jp.ymatsukawa.stockapi.domain.entity.db.Information;
import jp.ymatsukawa.stockapi.domain.entity.db.Tag;
import jp.ymatsukawa.stockapi.domain.repository.InformationTagsRepository;
import jp.ymatsukawa.stockapi.domain.repository.TagRepository;
import jp.ymatsukawa.stockapi.tool.converter.ListConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jp.ymatsukawa.stockapi.domain.repository.InformationRepository;

/**
 * Service works for usecase, not for entity.
 */
@Service
public class InformationService {
  @Autowired
  private InformationRepository informationRepository;
  @Autowired
  private TagRepository tagRepository;
  @Autowired
  private InformationTagsRepository informationTagsRepository;

  /**
   * Get list of information. <br />
   * When information is not registered, empty list [] is returned.
   * @param limit limit of getting information.
   * @param tags tags information has. blank, single word or comma separated.
   * @param sort sort object, "id" or "date"
   * @param sortBy sort way "asc" or "desc"
   * @return List of Information entity.
   * @return empty list when information is not registered.
   * @throws Exception when error occurs at process of RDBMS.
   */
  @Transactional
  public List<BridgeInformation> getAll(
    long limit, String tags, String sort, String sortBy
  ) throws Exception {
    // TODO: put footprint > if requested and no changed, return cached one.

    Set<String> tag = null;
    if(!tags.isEmpty()) {
      tag = new HashSet<>(ListConverter.getListBySplit(tags, ","));
    }

    // get all information data by parameter
    List<Information> information = informationRepository.findAll(limit, tag, sort, sortBy);

    // return empty Information if record is not found and do not step out,
    // because below steps need information entity
    if(information.isEmpty()) {
      return new ArrayList<BridgeInformation>() {};
    }

    // map informationid and tagname and store them by informationtag bean
    // in order to collect data "what informationid has tagnames"
    // { informationid1 -> tagname, informationid1 -> tagname, informationid2 -> tagname ... }
    List<Long> infoIds = information.stream().map(info -> info.getInformationId()).collect(Collectors.toList());
    List<BridgeInformationTags> informationTags = informationTagsRepository.findTagNameByInfomationIds(infoIds);

    // order List<BridgeInformationTags> to map; informationid to listed tagname; { informationid1 -> List<String>:tagname ... }
    // to prepare making "information's taglist"
    Map<Long, List<String>> tagsInfoHas = new HashMap<>();
    informationTags.forEach(informationTag -> {
      Long id = informationTag.getInformationId();
      String newTag = informationTag.getTag();

      if(tagsInfoHas.get(id) == null) {
        tagsInfoHas.put(id, new ArrayList<String>() { { add(newTag); } });
      } else {
        List<String> newTags = tagsInfoHas.get(id);
        newTags.add(newTag);
        tagsInfoHas.put(id, newTags);
      }
    });

    // make entity list of Infomation that has tag list
    // List<BridgeInformation(subject:String, detail:String, tag:List<String>)>
    List<BridgeInformation> entities = new ArrayList<>();
    information.forEach(info -> {
      List<String> tagsOfInfo = tagsInfoHas.get(info.getInformationId());
      entities.add(new BridgeInformation(info, tagsOfInfo));
    });

    return entities;
  }

  /**
   * Registers information with tag.<br />
   * if tag(s) is not registered, save it as new.
   * @param information - Information entity. Required properties are "subject" and "detail".
   * @param tag - Tag entity. Required propery is "name".
   * @throws Exception - when error occurs at process of RDBMS.
   */
  @Transactional
  public BridgeInformation create(Information information, Tag tag) throws Exception {
    // save information
    informationRepository.save(information, information.getSubject(), information.getDetail());

    // get tagname which is not saved.
    // if found, store it.
    if(!tag.getName().isEmpty()) {
      Set<String> newAddedTagNames = new HashSet<>(ListConverter.getListBySplit(tag.getName(), ","));
      newAddedTagNames.removeAll(tagRepository.findSavedName(newAddedTagNames));
      if(!newAddedTagNames.isEmpty()) {
        tagRepository.save(newAddedTagNames);
      }
    }

    // chains relation between informationid and tagids
    if(!tag.getName().isEmpty()) {
      Set<String> tags = new HashSet<>(ListConverter.getListBySplit(tag.getName(), ","));
      informationTagsRepository.saveRelationByInfoIdAndTagNames(information.getInformationId(), tags);
    }

    // return bridge entity
    return (new BridgeInformation(information, ListConverter.getListBySplit(tag.getName(), ",")));
  }
}
