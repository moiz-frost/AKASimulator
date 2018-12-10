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
        OUTER:
        while (input.hasNext()) {
            String line = input.nextLine().trim();
            String code = line;
            line = line.split("#")[0].trim();
            String[] split = line.split(":");
            if (split.length > 1) {
                dataLabels.put(split[0], machine.mp);
                line = split[1].trim();
            }
            split = line.split("[^\\w\\(\\)$+\\.-]+");
            switch (split[0]) {
                case ".word":
                case ".half":
                case ".byte":
                    for (int j = 1; j < split.length; j++) {
                        machine.memory[machine.mp++] = Integer.parseInt(split[j]);
                    }
                    break;
                case ".asciiz":
                case ".ascii":
                    boolean k = false;
                    split[1] = split[1].replaceAll("\"", "");
                    String param = code.substring(code.indexOf("\"") + 1, code.lastIndexOf("\""));
                    param = param.replace("\\n", "\n");
                    for (int j = 0; j < param.length(); j++, k = !k) {
                        if (!k) {
                            machine.memory[machine.mp] = param.charAt(j) << 16;
                        } else {
                            machine.memory[machine.mp++] += param.charAt(j);
                        }
                    }
                    if (!k) {
                        machine.memory[machine.mp++] = '\u0000';
                    } else {
                        machine.memory[machine.mp++] += '\u0000';
                    }
                    break;
                case ".space":
                    machine.mp += Math.ceil(Integer.parseInt(split[1]) / 4);
                    break;
                case ".align":
                    break;
                case ".text":
                    break OUTER;
            }
        }
        while (input.hasNext()) {
            String line = input.nextLine().trim();
            String[] hashSplit = line.split("#.*");
            if (hashSplit.length == 0 || line.length() == 0) {
                continue;
            }
            line = hashSplit[0].trim();
            if (line.contains(":")) {
                String[] split = line.split(":");
                codeLabels.put(split[0], code.size());
                if (split.length > 1) {
                    line = split[1];
                } else {
                    continue;
                }
            }
            code.add(line);
        }
    }

    void parseLine(int pc) {
        String line = code.get(pc);
        String[] tokens = line.split("[^\\w\\(\\)$+\\.-]+");
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
                machine.registers[machine.regMap.get(tokens[1])] = machine.memory[machine.registers[machine.regMap.get(addr)] + Integer.parseInt(offset)];
                break;
            case "sw":
                resplit = tokens[2].split("\\(");
                offset = resplit[0];
                addr = resplit[1].replaceAll("\\)", "");
                machine.memory[machine.registers[machine.regMap.get(addr)] + Integer.parseInt(offset)] = machine.registers[machine.regMap.get(tokens[1])];
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
                } catch (NumberFormatException e) {
                    jumpTo = codeLabels.get(tokens[1]);
                }
                machine.registers[32] = jumpTo - 1;
                break;
            case "jr":
                machine.registers[32] = machine.registers[machine.regMap.get(tokens[1])];
                break;
            case "jal":
                try {
                    jumpTo = Integer.parseInt(tokens[1]);
                } catch (NumberFormatException e) {
                    jumpTo = codeLabels.get(tokens[1]);
                }
                machine.registers[32] = jumpTo - 1;
                machine.registers[31] = pc;
                break;
            case "syscall":
                switch (machine.registers[2]) {
                    case 1:
                        machine.print_int();
                        break;
                    case 4:
                        machine.print_string();
                        break;
                    case 5:
                        machine.read_int();
                        break;
                    case 8:
                        machine.read_string();
                        break;
                    case 9:
                        machine.registers[2] = machine.mp;
                    case 10:
                    case 17:
                        machine.registers[32] = code.size();
                        break;
                }
                break;
            default:
                System.out.println("Invalid command: " + tokens[0] + " on line#" + pc);
                machine.registers[32] = code.size();
        }
    }
}
