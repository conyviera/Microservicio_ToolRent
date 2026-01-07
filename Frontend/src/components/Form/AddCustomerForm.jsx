import React, { useState } from 'react'; 
import customerService from '../../services/customer.services';
import Logo from '../../image/logo.png';
import '../../App.css';


function AddCustomerForm({ onCustomerAdded }) {

    const [name, setName] = useState('');
    const [email, setEmail] = useState('');
    const [phoneNumber, setPhoneNumber] = useState('');    
    const [rut, setRut] = useState('');

    const clearForm = () => {
        setName('');
        setEmail('');
        setPhoneNumber('');
        setRut('');
    };

    const handleSubmit = async (event) => {
        event.preventDefault();
        
        const customerData = {
            name: name,
            email: email,
            phoneNumber: phoneNumber,
            rut: rut
        };
        
        try {
            const response = await customerService.createCustomer(customerData);
            alert('¡Cliente agregado con éxito!');
            clearForm(); 
            onCustomerAdded(); 

        } catch (error) {

            if (error.response && error.response.data) {
                alert(error.response.data); 
            } else {
                alert('Hubo un error de conexión al agregar al cliente.');
            }
        } 
    };

    return (
    <div className= "form-container">
        <form className= "form-data" onSubmit={handleSubmit}>
            {/* ... (Tu logo está bien) ... */}
            <h3 className='title-input'>Agregar Nuevo Cliente</h3>
            
            <label>
                Rut:
                <input
                    className= "input-style"
                    placeholder='12345678-9'
                    type="text"
                    value={rut}
                    onChange={(e) => setRut(e.target.value)}
                />
            </label>
            <label>
                Nombre:
                <input
                    className= "input-style"
                    type="text"
                    placeholder="Nombre"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                /> 
            </label> <br/>
            <label>
                Email:
                <input
                    className= "input-style"
                    placeholder='ejemplo@gmail.com'
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                />
            </label><br/>
            <label>
                Teléfono:
                <input
                    className= "input-style"
                    placeholder= '+56912345678'
                    type="text"
                    value={phoneNumber}
                    onChange={(e) => setPhoneNumber(e.target.value)}
                />
            </label><br/>
            <button className='button-style' type="submit">Agregar Cliente</button>
        </form>
    </div>
    );
}

export default AddCustomerForm;