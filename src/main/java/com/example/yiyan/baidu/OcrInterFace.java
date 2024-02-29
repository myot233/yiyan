package com.example.yiyan.baidu;

import java.io.IOException;
import java.nio.file.Path;

public interface OcrInterFace {
    public OcrResult getPlaceFromImage(Path path) throws IOException;

    public OcrResult getPlaceFromTicket(Path path) throws IOException;


}



