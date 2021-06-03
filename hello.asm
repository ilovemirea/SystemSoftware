section .text
    global main
    extern printf
    extern scanf
    
main:
    sub rsp, 40
    MOV RCX, promt_x
    MOV R11, printf
    CALL R11
 
    MOV RCX, scanf_format
    MOV RDX, x
    MOV R11, scanf
    CALL R11

    MOV RCX, promt_y
    MOV R11, printf
    CALL R11
 
    MOV RCX, scanf_format
    MOV RDX, y
    MOV R11, scanf
    CALL R11

    PUSH QWORD [x]
    PUSH QWORD [y]
    POP RBX
    POP RAX
    ADD RAX, RBX
    PUSH RAX

    PUSH QWORD 2
    POP RBX
    POP RAX
    ADD RAX, RBX
    PUSH RAX
    MOV RDX, 0
    POP RDX
    mov rcx, message
    mov r11, printf 
    call r11
    add rsp, 40
    ret

section .data
    scanf_format: db "%d", 0
    promt_x:  db "   Input x;", 0
    x: dq 0
    promt_y: db "   Input y;", 0
    y: dq 0
message:
    db 'Result %d' ,0