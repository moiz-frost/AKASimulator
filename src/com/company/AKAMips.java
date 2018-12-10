package com.company;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

// http://www.dsi.unive.it/~gasparetto/materials/MIPS_Instruction_Set.pdf
public class AKAMips {

    int ram[] = new int[1000];
    int registers[] = new int[35];
    Map<String,Integer> regMap;
    Parser parser;
    GUI gui;

    AKAMips(File file) {
        parser = new Parser(file);
        parser.machine = this;
        regMap = new LinkedHashMap<String,Integer>();
        regMap.put("$zero", 0);
        regMap.put("$at", 1);
        regMap.put("$v0", 2);
        regMap.put("$v1", 3);
        for (int i = 0; i < 4; i++) {
            regMap.put("$a"+(i), i+4);
        }
        for (int i = 0; i < 8; i++) {
            regMap.put("$a"+(i), i+8);
        }
        for (int i = 0; i < 8; i++) {
            regMap.put("$s"+(i), i+16);
        }
        regMap.put("$a8", 24);
        regMap.put("$a9", 25);
        regMap.put("$k0", 26);
        regMap.put("$k1", 27);
        regMap.put("$gp", 28);
        regMap.put("$sp", 29);
        regMap.put("$fp", 30);
        regMap.put("$ra", 31);
        regMap.put("$pc", 32);
        regMap.put("$hi", 33);
        regMap.put("$lo", 34);
        
    }

}
