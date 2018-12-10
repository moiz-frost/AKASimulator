package com.company;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

// http://www.dsi.unive.it/~gasparetto/materials/MIPS_Instruction_Set.pdf
public class AKAMips {

    public static void main(String[] args) {
        File file;
        file = new File("program.asm");
        AKAMips asm = new AKAMips(file);
        asm.exec();
        System.out.println("shit");
       
    }
    
    int[] memory = new int[1000];
    int registers[] = new int[35];
    Map<String, Integer> regMap;
    Parser parser;
    GUI gui;
    Scanner input;
    File prog;

    AKAMips(File file) {
        prog=file;
        input = new Scanner(System.in);
        parser = new Parser(this);
        regMap = new LinkedHashMap<String, Integer>();
        regMap.put("$zero", 0);
        regMap.put("$at", 1);
        regMap.put("$v0", 2);
        regMap.put("$v1", 3);
        for (int i = 0; i < 4; i++) {
            regMap.put("$a" + (i), i + 4);
        }
        for (int i = 0; i < 8; i++) {
            regMap.put("$t" + (i), i + 8);
        }
        for (int i = 0; i < 8; i++) {
            regMap.put("$s" + (i), i + 16);
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
        registers[29] = memory.length;

    }
    void exec(){
        for (registers[32] = 0; registers[32] < parser.code.size(); registers[32]++) {
            parser.parseLine(registers[32]);
        }
    }
    void print_int() {
        System.out.println(registers[4]);
    }

    void print_float() {
        System.out.println(registers[4]);
    }

    void print_string() {
        String s = "";
        int i = registers[4];
        for (boolean k = false;; k = !k) {
            if (!k) {
                char c = (char) (memory[i] >> 16);
                if (c == '\u0000') {
                    break;
                }
                s += c;
            } else {
                char c = (char) (memory[i++] & 0xFFFF);
                if (c == '\u0000') {
                    break;
                }
                s += c;
            }
        }
        System.out.println(s);
    }

    void read_int() {
        input.nextInt();
    }

    void read_float() {
        input.nextFloat();
    }

    void read_string() {
        input.next(".*\\n\\n");
    }
}
