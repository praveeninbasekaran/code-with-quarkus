@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {
        int status;
        String message;

        if (exception instanceof NotFoundException) {
            status = Response.Status.NOT_FOUND.getStatusCode();
            message = exception.getMessage();
        } else if (exception instanceof BadRequestException) {
            status = Response.Status.BAD_REQUEST.getStatusCode();
            message = exception.getMessage();
        } else {
            status = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
            message = "An unexpected error occurred.";
        }

        var error = new ErrorResponse(status, message);
        return Response.status(status).entity(error).type(MediaType.APPLICATION_JSON).build();
    }

    public static class ErrorResponse {
        public int status;
        public String message;

        public ErrorResponse(int status, String message) {
            this.status = status;
            this.message = message;
        }
    }
}