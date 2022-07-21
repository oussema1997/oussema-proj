package com.sfm.obd.exception;

public class EntityNotFoundException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

//	public EntityNotFoundException(String entity, Long id) {
//        super(String.format(entity + " avec l'id %d est introuvable", id));
//    }
    
    public EntityNotFoundException(String message) {
        super(message);
    }
}
