package src.exceptionhandler;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import src.domain.exception.EntidadeNaoEncontradaException;
import src.domain.exception.NegocioException;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

	@Autowired
	private MessageSource messageSource;

	@ExceptionHandler(EntidadeNaoEncontradaException.class)
	public ResponseEntity<Object> handleEtidadeNaoEcontrada(NegocioException ex, WebRequest request) {
		var status = HttpStatus.NOT_FOUND;

		return handleExceptionInternal(ex, problema(status, ex.getMessage(), null), new HttpHeaders(), status, request);
	}

	@ExceptionHandler(NegocioException.class)
	public ResponseEntity<Object> handleNegocio(NegocioException ex, WebRequest request) {
		var status = HttpStatus.BAD_REQUEST;

		return handleExceptionInternal(ex, problema(status, ex.getMessage(), null), new HttpHeaders(), status, request);
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		var campos = new ArrayList<Problema.Campo>();

		for (ObjectError err : ex.getBindingResult().getAllErrors()) {
			String nome = ((FieldError) err).getField();
			String mensagem = messageSource.getMessage(err, LocaleContextHolder.getLocale());

			campos.add(new Problema.Campo(nome, mensagem));
		}

		return super.handleExceptionInternal(
				ex, problema(status,
						"Um ou mais campos estão invalidos. Faça o preenchimento correto e tente novamente", campos),
				headers, status, request);
	}

	private Problema problema(HttpStatus status, String msg, List<Problema.Campo> campos) {
		var problema = new Problema();
		problema.setStatus(status.value());
		problema.setTitulo(msg);
		problema.setDataHora(OffsetDateTime.now());
		problema.setCampos(campos);

		return problema;
	}

}
