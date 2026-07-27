import os

path = 'src/main/java/com/rrhh/dashboard/Empleados/controllers/GlobalExceptionHandler.java'
with open(path, 'r', encoding='utf-8') as f:
    content = f.read()

target = "    @ExceptionHandler(RuntimeException.class)"
replacement = """    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(
            org.springframework.security.access.AccessDeniedException ex, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("message", "Access Denied: " + ex.getMessage());
        response.put("status", HttpStatus.FORBIDDEN.value());
        response.put("path", request.getDescription(false).replace("uri=", ""));
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(RuntimeException.class)"""

if target in content and "handleAccessDeniedException" not in content:
    content = content.replace(target, replacement)
    with open(path, 'w', encoding='utf-8') as f:
        f.write(content)
    print("Added AccessDeniedException handler.")
else:
    print("Already added or target not found.")

