package com.company;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

// http://www.dsi.unive.it/~gasparetto/materials/MIPS_Instruction_Set.pdf
public class AKAMips {

    public static void main(String[] args) {
        File file;
        file = new File("Math.asm");
        AKAMips asm = new AKAMips();
        asm.loadFile(file);
        asm.exec();
        file = new File("program.asm");
        asm = new AKAMips();
        asm.loadFile(file);
        asm.exec();
        file = new File("IO.asm");
        asm = new AKAMips();
        asm.loadFile(file);
        asm.exec();
        file = new File("fibonacci.asm");
        asm = new AKAMips();
        asm.loadFile(file);
        asm.exec();
        file = new File("loop.asm");
        asm = new AKAMips();
        asm.loadFile(file);
        asm.exec();
    }

    int[] memory = new int[1000];
    int registers[] = new int[35];
    int mp = 0;
    Map<String, Integer> regMap;
    Parser parser;
    GUI gui;
    Scanner input;
    File prog;

    AKAMips() {
        input = new Scanner(System.in);
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
    void loadFile(File file){
        prog = file;
        parser = new Parser(this);
    }
    void exec() {
        for (registers[32] = 0; registers[32] < parser.code.size(); ) {
            parser.parseLine(registers[32]);
            registers[32]++;
        }
    }

    void print_int() {
        System.out.print(registers[4]);
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
        System.out.print(s);
    }
    void read_int() {
        registers[2] = input.nextInt();
    }
    void read_string() {
        String s = input.next(".*\\n\\n");
        int start = mp;
        boolean k = false;
        for (int j = 0; j < s.length(); j++, k = !k) {
            if (!k) {
                memory[mp] = s.charAt(j) << 16;
            } else {
                memory[mp++] += s.charAt(j);
            }
        }
        if (!k) {
            memory[mp++] = '\u0000';
        } else {
            memory[mp++] += '\u0000';
        }
        registers[2]=start;
        registers[3]=s.length();

    }

}
