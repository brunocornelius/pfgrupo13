package com.escritorio.entidadesFx;

import java.util.List;

import com.entidades.Rol;
import com.entidades.Usuario;
import com.enumerados.TipoDocumento;
import com.excepciones.DatosInvalidosException;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public class UsuarioFx implements EntidadFx {

    private final StringProperty nombres;
    private final StringProperty apellidos;
    private final StringProperty documento;
    private final StringProperty username;
    private final StringProperty password;
    private final BooleanProperty activo;
    private final StringProperty direccion;
    private final StringProperty correo;
    private final SimpleObjectProperty<TipoDocumento> tipoDocumento;
    private final IntegerProperty id;
    
    //Tiene q ser observable para asignarla al combo
    private ObservableList<RolFx> rolesFx = FXCollections.observableArrayList();
    
    private Usuario usuario;
    
    public UsuarioFx() {
    	this.nombres = new SimpleStringProperty("");
        this.apellidos = new SimpleStringProperty("");
        this.id = new SimpleIntegerProperty(0);
        this.username  = new SimpleStringProperty("");
        this.password  = new SimpleStringProperty("");
        this.tipoDocumento = new SimpleObjectProperty<TipoDocumento>();
        this.documento = new SimpleStringProperty("");
        this.usuario = new Usuario();
        this.activo = new SimpleBooleanProperty(true);
        this.direccion = new SimpleStringProperty("");
        this.correo = new SimpleStringProperty("");
    }

    public ObservableList<RolFx> getRolesFx() {
		return rolesFx;
	}


    
    public UsuarioFx(Usuario usuario) {
    	this.usuario = usuario;
        this.nombres = new SimpleStringProperty(usuario.getNombres());
        this.apellidos = new SimpleStringProperty(usuario.getApellidos());
        this.id = new SimpleIntegerProperty(usuario.getId());
        this.username  = new SimpleStringProperty(usuario.getUsername());
        this.password  = new SimpleStringProperty(usuario.getPassword());
        this.tipoDocumento = new SimpleObjectProperty<TipoDocumento>(usuario.getTipoDocumento());
        this.documento = new SimpleStringProperty(usuario.getDocumento());
        this.activo = new SimpleBooleanProperty(usuario.getActivo());
        this.direccion = new SimpleStringProperty(usuario.getDireccion());
        this.correo = new SimpleStringProperty(usuario.getCorreo());
    }
    
    public void cargarTodosLosRoles(List<Rol> roles) {
    	//por cada rol de la lista de roles, genero el RolFx
    	this.rolesFx = FXCollections.observableArrayList();
		for (Rol rol : roles) {
			RolFx rolFx = new RolFx(rol);
			rolFx.setSelected(this.usuario.getRoles().contains(rol));	//Seteo seleccionado según contenga o no el rol
			this.rolesFx.add(rolFx);
		}
    }

    public String getNombres() {
//        return nombre.get();
    	return this.usuario.getNombres();
    }

    public void setNombres(String nombres) {
        this.nombres.set(nombres);
        this.usuario.setNombres(nombres);
    }

    public StringProperty nombresProperty() {
        return nombres;
    }

    public String getApellidos() {
//        return apellidos.get();
    	return this.usuario.getApellidos();
    }

    public void setApellidos(String apellidos) {
        this.apellidos.set(apellidos);
        this.usuario.setApellidos(apellidos);
    }

    public StringProperty apellidosProperty() {
        return apellidos;
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }
    
    

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario p) {
		this.usuario = p;
		this.nombres.set(p.getNombres());
		this.apellidos.set(p.getApellidos());
		this.documento.set(p.getDocumento());
		this.username.set(p.getUsername());
		this.password.set(p.getPassword());
		this.id.set(p.getId());
	}

	@Override
	public boolean losCamposSonValidos() throws DatosInvalidosException {
		//La validacion se hace sobre los campos FX
		this.sincronizarRoles();
		String errorMessage = "";
		//TODO chequear largo maximo en el nombre y apellidos
		
		
		
        if (this.usuario.getNombres() == null || this.nombres.get().length() == 0) {
            errorMessage += "No has ingresado un nombre de Usuario válido\n"; 
        }else {
        	if ( this.usuario.getNombres().length() > 40) {
                errorMessage += "El largo de nombre no puede exceder los 40 caracteres\n"; 
            }
        }
        
        
        if (this.usuario.getApellidos() == null || this.apellidos.get().length() == 0) {
            errorMessage += "No has ingresado una apellidos de Usuario válido\n"; 
        }else {
        	if ( this.usuario.getApellidos().length() > 40) {
                errorMessage += "El largo de apellidos no puede exceder los 40 caracteres\n"; 
            }
        }
        if (this.usuario.getPassword() == null || this.password.get().length() == 0) {
            errorMessage += "La contraseña no puede ser nula\n"; 
        }
        if (this.usuario.getTipoDocumento() == null ) {
        	errorMessage += "Seleccione un tipo de Documento\n";
        }
        if (this.usuario.getDocumento() == null || this.usuario.getDocumento().length() == 0) {
            errorMessage += "El documento no es válido\n"; 
        } 
        if (this.username.get() == null || this.username.get().length() == 0) {
            errorMessage += "El usuario no es válido\n"; 
        }
        if (this.usuario.getRoles().size()==0) {
        	errorMessage += "El usuario no tiene un rol asignado\n"; 
        }
        if ( !( this.usuario.getCorreo().contains("@") && this.usuario.getCorreo().contains(".") ) ) {
        	//Si no contiene @ y .
        	errorMessage += "El correo no es válido\n"; 
        }
        
        if (errorMessage.length() == 0) {
            return true;
        } else {
        	throw new DatosInvalidosException(errorMessage);
        }
		
        //Ya no llegaría nunca aqui.
	}

	public final StringProperty usernameProperty() {
		return this.username;
	}
	

	public final String getUsername() {
		return this.usuario.getUsername();
	}
	

	public final void setUsername(final String username) {
		this.usernameProperty().set(username);
		this.usuario.setUsername(username);
	}
	

	public final StringProperty passwordProperty() {
		return this.password;
	}
	

	public final String getPassword() {
		return this.usuario.getPassword();
	}
	

	public final void setPassword(final String password) {
		this.passwordProperty().set(password);
		this.usuario.setPassword(password);
	}

	public final SimpleObjectProperty<TipoDocumento> tipoDocumentoProperty() {
		return this.tipoDocumento;
	}
	

	public final TipoDocumento getTipoDocumento() {
		return this.usuario.getTipoDocumento();
	}
	
	
	

	public final void setTipoDocumento(final TipoDocumento tipoDocumento) {
		this.tipoDocumentoProperty().set(tipoDocumento);
		this.usuario.setTipoDocumento(tipoDocumento);
	}

	public final StringProperty documentoProperty() {
		return this.documento;
	}
	

	public final String getDocumento() {
		return this.usuario.getDocumento();
	}
	

	public final void setDocumento(final String documento) {
		this.documentoProperty().set(documento);
		this.usuario.setDocumento(documento);
	}
	
	public void sincronizarRoles() {
		
		for (RolFx rolFx : rolesFx) {
			boolean selected = rolFx.isSelected();
			if (selected) {
				//Si lo selecciono
				this.usuario.addRol(rolFx.getRol());
			}else {
				this.usuario.removeRol(rolFx.getRol());
			}
			System.out.println(rolFx.getNombre() + ":" + selected + " en " + this.getNombres());
		}
		
	}

	public final BooleanProperty activoProperty() {
		return this.activo;
	}
	

	public final boolean isActivo() {
		this.activoProperty().set(this.usuario.getActivo());
		return this.activoProperty().get();
	}
	

	public final void setActivo(final boolean activo) {
		this.activoProperty().set(activo);
		this.usuario.setActivo(activo);
	}

	public final StringProperty direccionProperty() {
		return this.direccion;
	}
	

	public final String getDireccion() {
		this.direccionProperty().set(this.usuario.getDireccion());
		return this.direccionProperty().get();
	}
	

	public final void setDireccion(final String direccion) {
		this.usuario.setDireccion(direccion);
		this.direccionProperty().set(direccion);
	}
	

	public final StringProperty correoProperty() {
		
		return this.correo;
	}
	

	public final String getCorreo() {
		this.correoProperty().set(this.usuario.getCorreo());
		return this.correoProperty().get();
	}
	

	public final void setCorreo(final String correo) {
		this.usuario.setCorreo(correo);
		this.correoProperty().set(correo);
	}
	
	public String getStringRoles() {
		String roles = "";
		for (Rol rol : this.usuario.getRoles()) {

			roles = roles.concat(rol.getNombre()).concat("|");

			
		}
		return roles;
	}
	
}