.text
li $v0, 5
syscall
move $t0, $v0
li $v0, 5
syscall
add $a0, $t0, $v0
li $v0, 1
syscall
