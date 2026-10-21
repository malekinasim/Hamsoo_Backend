//package com.hamsoo.bpm.service.impl.modelconnectorelement;
//
//
//import com.hamsoo.bpm.model.entity.modelconnectorelement.ModelConnectorElement;
//import com.hamsoo.bpm.repository.jpa.modelconnectorelement.ModelConnectorElementRepository;
//import com.hamsoo.bpm.service.api.modelconnectorelement.ModelConnectorElementService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.stereotype.Service;
//
//import javax.persistence.NoResultException;
//import java.io.UnsupportedEncodingException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class ModelConnectorElementServiceImpl implements ModelConnectorElementService {
//
//
//    @Autowired
//    public ModelConnectorElementRepository modelConnectorElementRepository;
//
//    @Override
//    public Optional<ModelConnectorElement> find(Long id) {
//        try {
//            return modelConnectorElementRepository.findById(id);
//        } catch (Exception e) {
//
//            return null;
//        }
//    }
//
//    @Override
//    public int save(ModelConnectorElement modelConnectorElement) {
//        try {
//            modelConnectorElementRepository.save(modelConnectorElement);
//            return 1;
//        } catch (Exception e) {
//
//            return 0;
//        }
//
//    }
//
//    @Override
//    public int saveAll(List<ModelConnectorElement> modelConnectorElementList) {
//        try {
//            modelConnectorElementRepository.saveAll(modelConnectorElementList);
//            return 1;
//        } catch (Exception e) {
//
//            return 0;
//        }
//    }
//
//    @Override
//    public int update(ModelConnectorElement modelConnectorElement) {
//        try {
//            ModelConnectorElement foundModelConnectorElement = modelConnectorElementRepository.findById(modelConnectorElement.getId()).orElse(null);
//            // update from modelConnectorElement fields on corresponding foundModelConnectorElement fields, e.g.:
//            // foundModelConnectorElement.setABC(modelConnectorElement.getABC());
//
//            modelConnectorElementRepository.save(foundModelConnectorElement);
//            return 1;
//        } catch (Exception e) {
//
//            return 0;
//        }
//    }
//
//    @Override
//    public int updateAll(List<ModelConnectorElement> modelConnectorElementList) {
//
//        List<ModelConnectorElement> foundModelConnectorElementList = new ArrayList<>();
//        ModelConnectorElement foundModelConnectorElement;
//        for (ModelConnectorElement modelConnectorElement : modelConnectorElementList) {
//            foundModelConnectorElement = modelConnectorElementRepository.findById(modelConnectorElement.getId()).orElse(null);
//            // update from modelConnectorElement fields on corresponding foundModelConnectorElement fields, e.g.:
//            // foundModelConnectorElement.setABC(modelConnectorElement.getABC());
//
//            foundModelConnectorElementList.add(foundModelConnectorElement);
//        }
//
//        try {
//            modelConnectorElementRepository.saveAll(foundModelConnectorElementList);
//            return 1;
//        } catch (Exception e) {
//
//            return 0;
//        }
//    }
//
//    @Override
//    public int delete(Long id) {
//        try {
//            modelConnectorElementRepository.deleteById(id);
//            return 1;
//        } catch (Exception e) {
//
//            return 0;
//        }
//    }
//
//    @Override
//    public List<ModelConnectorElement> getList() {
//        try {
//            return modelConnectorElementRepository.findAll();
//        } catch (NoResultException e) {
//
//            return null;
//        }
//    }
//
//    @Override
//    public Page<ModelConnectorElement> gridLoad(String search, Pageable pageable, String orderBy, String direction) {
//        try {
//            Pageable gridPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
//
//            return modelConnectorElementRepository.findAll(gridPageable);
//
//        } catch (NoResultException e) {
//
//            return null;
//        }
//    }
//
//}