package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class Parser {

    ArrayList<String> code;
    Scanner input;
    AKAMips machine;
    Map<String, Integer> labels;

    Parser(File file) {
        code = new ArrayList<String>();
        labels = new LinkedHashMap<String, Integer>();
        try {
            input = new Scanner(file);
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found.");
        }
        while (input.hasNext()) {
            String line = input.nextLine();
            String[] split = line.split(":");
            if (split.length > 1) {
                labels.put(split[0], code.size());
                line = split[1];
            }
            code.add(line);
        }
    }

    void parseLine(int pc) {
        String line = code.get(pc);
        String[] tokens = line.split("[^\\w\\(\\)$]*");
        String[] resplit;
        String addr;
        String offset;
        int jumpTo;
        switch (tokens[0]) {
            case "add":
                machine.registers[machine.regMap.get(tokens[1])] = machine.registers[machine.regMap.get(tokens[2])] + machine.registers[machine.regMap.get(tokens[3])];
                break;
            case "sub":
                machine.registers[machine.regMap.get(tokens[1])] = machine.registers[machine.regMap.get(tokens[2])] - machine.registers[machine.regMap.get(tokens[3])];
                break;
            case "addi":
                machine.registers[machine.regMap.get(tokens[1])] = machine.registers[machine.regMap.get(tokens[2])] + Integer.parseInt(tokens[3]);
                break;
            case "addu":
                machine.registers[machine.regMap.get(tokens[1])] = machine.registers[machine.regMap.get(tokens[2])] + machine.registers[machine.regMap.get(tokens[3])]; // Not Supported
                break;
            case "subu":
                machine.registers[machine.regMap.get(tokens[1])] = machine.registers[machine.regMap.get(tokens[2])] - machine.registers[machine.regMap.get(tokens[3])]; // Not Supported
                break;
            case "addiu":
                machine.registers[machine.regMap.get(tokens[1])] = machine.registers[machine.regMap.get(tokens[2])] + machine.regMap.get(tokens[3]); // Not Supported
                break;
            case "mul":
                machine.registers[machine.regMap.get(tokens[1])] = machine.registers[machine.regMap.get(tokens[2])] * machine.registers[machine.regMap.get(tokens[3])];
                break;
            case "mult":
                long product = machine.registers[machine.regMap.get(tokens[1])] * machine.registers[machine.regMap.get(tokens[2])];
                int hi = (int) (product >> 32);
                int lo = (int) (product & 0xFFFFFFFF);
                machine.registers[33] = hi;
                machine.registers[34] = lo;
                break;
            case "div":
                machine.registers[33] = machine.registers[machine.regMap.get(tokens[1])] % machine.registers[machine.regMap.get(tokens[2])];
                machine.registers[34] = machine.registers[machine.regMap.get(tokens[1])] / machine.registers[machine.regMap.get(tokens[2])];
                break;
            case "and":
                machine.registers[machine.regMap.get(tokens[1])] = machine.registers[machine.regMap.get(tokens[2])] & machine.registers[machine.regMap.get(tokens[3])];
                break;
            case "or":
                machine.registers[machine.regMap.get(tokens[1])] = machine.registers[machine.regMap.get(tokens[2])] | machine.registers[machine.regMap.get(tokens[3])];
                break;
            case "andi":
                machine.registers[machine.regMap.get(tokens[1])] = machine.registers[machine.regMap.get(tokens[2])] & Integer.parseInt(tokens[3]);
                break;
            case "ori":
                machine.registers[machine.regMap.get(tokens[1])] = machine.registers[machine.regMap.get(tokens[2])] | Integer.parseInt(tokens[3]);
                break;
            case "sll":
                machine.registers[machine.regMap.get(tokens[1])] = machine.registers[machine.regMap.get(tokens[2])] << Integer.parseInt(tokens[3]);
                break;
            case "srl":
                machine.registers[machine.regMap.get(tokens[1])] = machine.registers[machine.regMap.get(tokens[2])] >> Integer.parseInt(tokens[3]);
                break;
            case "lw":
                resplit = tokens[2].split("\\(");
                offset = resplit[0];
                addr = resplit[1].replaceAll("\\)", "");
                machine.registers[machine.regMap.get(tokens[1])] = machine.ram[Integer.parseInt(addr) + Integer.parseInt(offset)];
                break;
            case "sw":
                resplit = tokens[2].split("\\(");
                offset = resplit[0];
                addr = resplit[1].replaceAll("\\)", "");
                machine.ram[Integer.parseInt(addr) + Integer.parseInt(offset)] = machine.registers[machine.regMap.get(tokens[1])];
                break;
            case "lui":
                machine.registers[machine.regMap.get(tokens[1])] = machine.registers[Integer.parseInt(tokens[2])] << 16;
                break;
            case "la":
                code.set(pc, "lui $at, 4097");
                code.add(pc + 1, "ori " + tokens[1] + ", $at, ");
                machine.registers[32]--;
                break;
            case "li":
                machine.registers[machine.regMap.get(tokens[1])] = Integer.parseInt(tokens[2]);
                break;
            case "mfhi":
                machine.registers[machine.regMap.get(tokens[1])] = machine.registers[33];
                break;
            case "mflo":
                machine.registers[machine.regMap.get(tokens[1])] = machine.registers[34];
                break;
            case "move":
                machine.registers[machine.regMap.get(tokens[1])] = machine.registers[machine.regMap.get(tokens[2])];
                break;
            case "beq":
                if (machine.registers[machine.regMap.get(tokens[1])] == machine.registers[machine.regMap.get(tokens[2])]) {
                    try {
                        jumpTo = Integer.parseInt(tokens[3]);
                        machine.registers[32] = jumpTo;
                    } catch (NumberFormatException e) {
                        jumpTo = labels.get(tokens[3]);
                        machine.registers[32] = jumpTo;
                    }
                }
                break;
            case "bne":
                if (machine.registers[machine.regMap.get(tokens[1])] != machine.registers[machine.regMap.get(tokens[2])]) {
                    try {
                        jumpTo = Integer.parseInt(tokens[3]);
                        machine.registers[32] = jumpTo;
                    } catch (NumberFormatException e) {
                        jumpTo = labels.get(tokens[3]);
                        machine.registers[32] = jumpTo;
                    }
                }
                break;
            case "bgt":
                if (machine.registers[machine.regMap.get(tokens[1])] > machine.registers[machine.regMap.get(tokens[2])]) {
                    try {
                        jumpTo = Integer.parseInt(tokens[3]);
                        machine.registers[32] = jumpTo;
                    } catch (NumberFormatException e) {
                        jumpTo = labels.get(tokens[3]);
                        machine.registers[32] = jumpTo;
                    }
                }
                break;
            case "bge":
                if (machine.registers[machine.regMap.get(tokens[1])] >= machine.registers[machine.regMap.get(tokens[2])]) {
                    try {
                        jumpTo = Integer.parseInt(tokens[3]);
                        machine.registers[32] = jumpTo;
                    } catch (NumberFormatException e) {
                        jumpTo = labels.get(tokens[3]);
                        machine.registers[32] = jumpTo;
                    }
                }
                break;
            case "blt":
                if (machine.registers[machine.regMap.get(tokens[1])] < machine.registers[machine.regMap.get(tokens[2])]) {
                    try {
                        jumpTo = Integer.parseInt(tokens[3]);
                        machine.registers[32] = jumpTo;
                    } catch (NumberFormatException e) {
                        jumpTo = labels.get(tokens[3]);
                        machine.registers[32] = jumpTo;
                    }
                }
                break;
            case "ble":
                if (machine.registers[machine.regMap.get(tokens[1])] <= machine.registers[machine.regMap.get(tokens[2])]) {
                    try {
                        jumpTo = Integer.parseInt(tokens[3]);
                        machine.registers[32] = jumpTo;
                    } catch (NumberFormatException e) {
                        jumpTo = labels.get(tokens[3]);
                        machine.registers[32] = jumpTo;
                    }
                }
                break;
            case "slt":
                if (machine.registers[machine.regMap.get(tokens[2])] < machine.registers[machine.regMap.get(tokens[3])]) {
                    machine.registers[machine.regMap.get(tokens[1])] = 1;
                } else {
                    machine.registers[machine.regMap.get(tokens[1])] = 0;
                }
                break;
            case "slti":
                if (machine.registers[machine.regMap.get(tokens[2])] < Integer.parseInt(tokens[3])) {
                    machine.registers[machine.regMap.get(tokens[1])] = 1;
                } else {
                    machine.registers[machine.regMap.get(tokens[1])] = 0;
                }
                break;
            case "j":
                try {
                    jumpTo = Integer.parseInt(tokens[1]);
                    machine.registers[32] = jumpTo;
                } catch (NumberFormatException e) {
                    jumpTo = labels.get(tokens[1]);
                    machine.registers[32] = jumpTo;
                }
                break;
            case "jr":
                machine.registers[32] = machine.registers[machine.regMap.get(tokens[1])];
                break;
            case "jal":
                try {
                    jumpTo = Integer.parseInt(tokens[1]);
                    machine.registers[32] = jumpTo;
                } catch (NumberFormatException e) {
                    jumpTo = labels.get(tokens[1]);
                    machine.registers[32] = jumpTo;
                }
                machine.registers[31]=pc;
                break;
            case "syscall":              
                break;
        }
    }
}
