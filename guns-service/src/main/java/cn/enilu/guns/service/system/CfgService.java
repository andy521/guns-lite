package cn.enilu.guns.service.system;

import cn.enilu.guns.bean.entity.system.Cfg;
import cn.enilu.guns.dao.cache.ConfigCache;
import cn.enilu.guns.dao.system.CfgRepository;
import cn.enilu.guns.utils.StringUtils;
import cn.enilu.guns.utils.factory.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * CfgService
 *
 * @author enilu
 * @version 2018/11/17 0017
 */

@Service
@Transactional
public class CfgService {
    @Autowired
    private CfgRepository cfgRepository;
    @Autowired
    private ConfigCache configCache;
    public Page findPage(Page<Cfg> page ,final Map<String,String> params) {
        Pageable pageable = null;
        if(page.isOpenSort()) {
            pageable = new PageRequest(page.getCurrent()-1, page.getSize(), page.isAsc() ? Sort.Direction.ASC : Sort.Direction.DESC, page.getOrderByField());
        }else{
            pageable = new PageRequest(page.getCurrent()-1,page.getSize());
        }

        org.springframework.data.domain.Page<Cfg> pageResult = cfgRepository.findAll(new Specification<Cfg>(){


            @Override
            public Predicate toPredicate(Root<Cfg> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<Predicate>();
                if(StringUtils.isNotEmpty(params.get("cfgName"))){
                    list.add(criteriaBuilder.like(root.get("cfgName").as(String.class),"%"+params.get("cfgName")+"%"));
                }

                if(StringUtils.isNotEmpty(params.get("cfgValue"))){
                    list.add(criteriaBuilder.like(root.get("cfgValue").as(String.class),"%"+params.get("cfgValue")
                            +"%"));
                }
                Predicate[] p = new Predicate[list.size()];
                return criteriaBuilder.and(list.toArray(p));
            }
        },pageable);
        page.setTotal(Integer.valueOf(pageResult.getTotalElements()+""));
        page.setRecords(pageResult.getContent());
        return page;
    }

    public void save(Cfg cfg) {
        cfgRepository.save(cfg);
        configCache.cache();
    }

    public void delete(Long id) {
        cfgRepository.delete(id);
        configCache.cache();
    }
}
