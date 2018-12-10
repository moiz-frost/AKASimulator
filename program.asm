.word 5,6,7,8
.space 4
SHIT: .ascii "babli"
.text
la $a0, SHIT
li $v0, 4
syscall
