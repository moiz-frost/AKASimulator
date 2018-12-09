package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Parser {
    ArrayList<String> code;
    Scanner input;
    AKAMips machine;

    Parser(File file){
        code = new ArrayList<String>();
        try {
            input = new Scanner(file);
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found.");
        }
        while (input.hasNext()){
            code.add(input.nextLine());
        }

    }

    void parseLine(int pc){
        String line = code.get(pc);
        String[] tokens = line.split(" ");
        switch (tokens[0]){
            case "add":
                machine.registers[Integer.parseInt(tokens[1])]=machine.registers[Integer.parseInt(tokens[2])]+machine.registers[Integer.parseInt(tokens[3])];
                break;
            case "sub":
                machine.registers[Integer.parseInt(tokens[1])]=machine.registers[Integer.parseInt(tokens[2])]-machine.registers[Integer.parseInt(tokens[3])];
                break;
            case "addi":
                machine.registers[Integer.parseInt(tokens[1])]=machine.registers[Integer.parseInt(tokens[2])]+Integer.parseInt(tokens[3]);
                break;
            case "addu":
                machine.registers[Integer.parseInt(tokens[1])]=machine.registers[Integer.parseInt(tokens[2])]+Integer.parseInt(tokens[3]);
                break;
            case "subu":
                machine.registers[Integer.parseInt(tokens[1])]=machine.registers[Integer.parseInt(tokens[2])]-Integer.parseInt(tokens[3]);
                break;
            case "addiu":
                machine.registers[Integer.parseInt(tokens[1])]=machine.registers[Integer.parseInt(tokens[2])]+Integer.parseInt(tokens[3]);
                break;
            case "mul":
                machine.registers[Integer.parseInt(tokens[1])]=machine.registers[Integer.parseInt(tokens[2])]*machine.registers[Integer.parseInt(tokens[3])];
                break;
            case "mult":
                long product = machine.registers[Integer.parseInt(tokens[1])]*machine.registers[Integer.parseInt(tokens[2])];
                int hi = (int) (product >> 32);
                int lo = (int) (product & 0xFFFFFFFF);
                machine.registers[33]= hi;
                machine.registers[34]= lo;
                break;
            case "div":
                machine.registers[33]= machine.registers[Integer.parseInt(tokens[1])]%machine.registers[Integer.parseInt(tokens[2])];
                machine.registers[34]= machine.registers[Integer.parseInt(tokens[1])]/machine.registers[Integer.parseInt(tokens[2])];
                break;
            case "and":
                machine.registers[Integer.parseInt(tokens[1])]=machine.registers[Integer.parseInt(tokens[2])] & machine.registers[Integer.parseInt(tokens[3])];
                break;
            case "or":
                machine.registers[Integer.parseInt(tokens[1])]=machine.registers[Integer.parseInt(tokens[2])] | machine.registers[Integer.parseInt(tokens[3])];
                break;
            case "andi":
                machine.registers[Integer.parseInt(tokens[1])]=machine.registers[Integer.parseInt(tokens[2])] & Integer.parseInt(tokens[3]);
                break;
            case "ori":
                machine.registers[Integer.parseInt(tokens[1])]=machine.registers[Integer.parseInt(tokens[2])] | Integer.parseInt(tokens[3]);
                break;
            case "sll":
                machine.registers[Integer.parseInt(tokens[1])]=machine.registers[Integer.parseInt(tokens[2])] << Integer.parseInt(tokens[3]);
                break;
            case "srl":
                machine.registers[Integer.parseInt(tokens[1])]=machine.registers[Integer.parseInt(tokens[2])] >> Integer.parseInt(tokens[3]);
                break;         
            case "lw":
                machine.registers[Integer.parseInt(tokens[1])]=machine.ram[Integer.parseInt(tokens[3])+Integer.parseInt(tokens[2])];
                break;
            case "sw":
                machine.ram[Integer.parseInt(tokens[3])+Integer.parseInt(tokens[2])]=machine.registers[Integer.parseInt(tokens[1])];
                break;
            case "lui":
                machine.registers[Integer.parseInt(tokens[1])]=machine.registers[Integer.parseInt(tokens[2])]<<16;
                break;
            case "la":

                break;
            case "li":
                break;
            case "mfhi":
                break;
            case "mflo":
                break;
            case "move":
                break;
            case "beq":
                break;
            case "bne":
                break;
            case "bgt":
                break;
            case "bge":
                break;
            case "blt":
                break;
            case "ble":
                break;
            case "slt":
                break;
            case "slti":
                break;
            case "j":
                break;
            case "jr":
                break;
            case "jal":
                break;
            case "syscall":
                break;
        }
    }
}
