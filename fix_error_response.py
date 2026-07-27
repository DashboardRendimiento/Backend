import os

path = 'src/main/java/com/rrhh/dashboard/Empleados/dtos/ErrorResponse.java'
with open(path, 'r', encoding='utf-8') as f:
    content = f.read()

# ErrorResponse has inner class ValidationError
if 'public ErrorResponse(java.time.LocalDateTime' not in content:
    content = content.replace('public class ErrorResponse {', '''public class ErrorResponse {
    public ErrorResponse(java.time.LocalDateTime timestamp, int status, String error, String message, String path, java.util.List<ValidationError> validationErrors) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.validationErrors = validationErrors;
    }
''', 1)

if 'public ValidationError(String' not in content:
    content = content.replace('public static class ValidationError {', '''public static class ValidationError {
        public ValidationError(String field, String message, Object rejectedValue) {
            this.field = field;
            this.message = message;
            this.rejectedValue = rejectedValue;
        }
''', 1)

with open(path, 'w', encoding='utf-8') as f:
    f.write(content)

