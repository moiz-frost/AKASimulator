package com.company;

import java.io.File;

// http://www.dsi.unive.it/~gasparetto/materials/MIPS_Instruction_Set.pdf
public class AKAMips {

    int ram[] = new int[1000];
    int registers[] = new int[35];
    Parser parser;
    GUI gui;

    AKAMips(File file) {
        parser = new Parser(file);
        parser.machine = this;
    }

}
