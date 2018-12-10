package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.management.MemoryType;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class Parser {

    ArrayList<String> code;
    Scanner input;
    AKAMips machine;
    Map<String, Integer> codeLabels;
    Map<String, Integer> dataLabels;

    Parser(AKAMips m) {
        machine = m;
        File file = m.prog;
        code = new ArrayList<String>();
        codeLabels = new LinkedHashMap<String, Integer>();
        dataLabels = new LinkedHashMap<String, Integer>();
        try {
            input = new Scanner(file);
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found.");
        }
        int i = 0;
        OUTER: while (input.hasNext()) {
            
            String line = input.nextLine().trim();
            line = line.split("#")[0].trim();
            String[] split = line.split(":");
            if (split.length > 1) {
                dataLabels.put(split[0], i);
                line = split[1].trim();
            }
            split = line.split("[^\\w\\(\\)$\\.]+");
            switch (split[0]) {
                case ".word":
                    for (int j = 1; j < split.length; j++) {
                        machine.memory[i++] = Integer.parseInt(split[j]);
                    }
                    break;
                case ".ascii":
                    boolean k = false;
                    split[1]=split[1].replaceAll("\"","");
                    for (int j = 0; j < split[1].length(); j++, k = !k) {
                        if (!k) {
                            machine.memory[i] = split[1].charAt(j) << 16;
                        } else {
                            machine.memory[i++] += split[1].charAt(j);
                        }
                    }
                    if (!k) {
                        machine.memory[i++] = '\u0000';
                    } else {
                        machine.memory[i++] += '\u0000';
                    }
                    break;
                case ".space":
                    i += Integer.parseInt(split[1]);
                    break;
                case ".text":
                    break OUTER;
            }
        }
        while (input.hasNext()) {
            String line = input.nextLine().trim();
            line = line.split("#")[0].trim();
            String[] split = line.split(":");
            if (split.length > 1) {
                codeLabels.put(split[0], code.size());
                line = split[1];
            }
            code.add(line);
        }
    }

    void parseLine(int pc) {
        String line = code.get(pc);
        String[] tokens = line.split("[^\\w\\(\\)$]+");
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
                machine.registers[machine.regMap.get(tokens[1])] = machine.memory[Integer.parseInt(addr) + Integer.parseInt(offset)];
                break;
            case "sw":
                resplit = tokens[2].split("\\(");
                offset = resplit[0];
                addr = resplit[1].replaceAll("\\)", "");
                machine.memory[Integer.parseInt(addr) + Integer.parseInt(offset)] = machine.registers[machine.regMap.get(tokens[1])];
                break;
            case "lui":
                machine.registers[machine.regMap.get(tokens[1])] = machine.registers[Integer.parseInt(tokens[2])] << 16;
                break;
            case "la":
//                code.set(pc, "lui $at, 4097");
//                code.add(pc + 1, "ori " + tokens[1] + ", $at, ");
//                machine.registers[32]--;
                machine.registers[machine.regMap.get(tokens[1])] = dataLabels.get(tokens[2]);
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
                        jumpTo = codeLabels.get(tokens[3]);
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
                        jumpTo = codeLabels.get(tokens[3]);
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
                        jumpTo = codeLabels.get(tokens[3]);
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
                        jumpTo = codeLabels.get(tokens[3]);
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
                        jumpTo = codeLabels.get(tokens[3]);
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
                        jumpTo = codeLabels.get(tokens[3]);
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
                    jumpTo = codeLabels.get(tokens[1]);
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
                    jumpTo = codeLabels.get(tokens[1]);
                    machine.registers[32] = jumpTo;
                }
                machine.registers[31] = pc;
                break;
            case "syscall":
                switch(machine.registers[2]){
                    case 1:
                        machine.print_int();
                        break;
                    case 2:
                        machine.print_float();
                        break;
                    case 4:
                        machine.print_string();
                        break;
                    case 5:
                        machine.read_int();
                        break;
                    case 6: 
                        machine.read_float();
                        break;
                    case 8:
                        machine.read_string();
                        break;
                }
                break;
        }
    }
}
