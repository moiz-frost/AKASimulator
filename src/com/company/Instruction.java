package com.company;

public class Instruction {
    int opcode;
    int[] params;

    Instruction(int o, int[] p){
        opcode = o;
        params = p;
    }
}
