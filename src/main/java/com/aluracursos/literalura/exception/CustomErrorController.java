package com.aluracursos.literalura.exception;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        
        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());
            
            if (statusCode == 404) {
                model.addAttribute("errorCode", "404");
                model.addAttribute("errorTitle", "Página no encontrada");
                model.addAttribute("errorMessage", "Lo sentimos, pero este libro (o página) se ha perdido en los confines de nuestra estantería digital.");
            } else if (statusCode == 500) {
                model.addAttribute("errorCode", "500");
                model.addAttribute("errorTitle", "Error Interno del Servidor");
                model.addAttribute("errorMessage", "Parece que hubo un cortocircuito en nuestra biblioteca. Ya estamos trabajando para repararlo.");
                
                // Logging de la excepción (si está disponible)
                Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
                if (exception != null) {
                    System.err.println("Excepción capturada por CustomErrorController: " + exception);
                }
            } else {
                model.addAttribute("errorCode", statusCode.toString());
                model.addAttribute("errorTitle", "Ha ocurrido un error");
                model.addAttribute("errorMessage", "Un error inesperado se ha cruzado en nuestro camino.");
            }
        } else {
            model.addAttribute("errorCode", "Error");
            model.addAttribute("errorTitle", "Error Desconocido");
            model.addAttribute("errorMessage", "Algo salió mal, pero no sabemos exactamente qué.");
        }
        
        return "error";
    }
}
