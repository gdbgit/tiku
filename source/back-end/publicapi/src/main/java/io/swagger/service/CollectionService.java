package io.swagger.service;

import io.swagger.model.CollectionInfo;
import io.swagger.model.Expression;
import io.swagger.pojo.PaperFullData;
import io.swagger.utils.ParserErrorException;

import java.util.List;


public interface CollectionService {
    List<PaperFullData> queryCollection(Expression expressione, boolean isDeep) throws ParserErrorException;

    Long addPaperByCollectionInfo(CollectionInfo collectionInfo) throws Exception;
}
