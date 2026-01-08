import React, {useState} from 'react';
import toolService from '../../services/tool.services'
import Logo from '../../image/logo.png'

function AddExistingToolForm({typeTool, onToolAdded}){
    
    const [quantity, setQuantity]= useState('');

    if (!typeTool) {
      return null; 
    }

    const clearForm = () => {
    setQuantity('');
    };

    const handleSubmit = async (event) => {
        event.preventDefault(); 
        const numQuantity = parseInt(quantity, 10);
        
        if (isNaN(numQuantity) || numQuantity <= 0) {
            alert("Por favor, ingresa una cantidad válida y mayor a 0.");
            return; 
        }
        const toolData= 
          {name: typeTool.name,
          category: typeTool.category,
          replacementValue: typeTool.replacementValue,
          dailyRate: typeTool.dailyRate,
          debtRate: typeTool.debtRate,
          amount: numQuantity
         };
    
        try {
        const response = await toolService.createLotTool(toolData);
        
        alert('¡Herramienta agregada con éxito!'); 
        clearForm();
        onToolAdded();
        
      } catch (error) {
        if (error.response && error.response.data) {
          alert(error.response.data); 
      } else {
          alert('Hubo un error de conexión al agregar la herramienta.');
      }
      }
    };

    return (
        <div className= "form-container">
          <form className= "form-data" onSubmit={handleSubmit}>
            <div style={{
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                }}>
            
                <img 
                  src={Logo}
                  alt="Logo" 
                  style={{ 
                  height: '90px' 
                  }}
              /></div>
    
             <h3 className='title-input'>Agregar a: <br/> {typeTool.name}</h3>
             <label>
              Cantidad a agregar:
              <input
                className= "input-style"
                type="number"
                value={quantity}
                onChange={(e) => {const value = e.target.value;
                  if (value === '' || Number(value) >= 0) { 
                    setQuantity(e.target.value) 
                  }}}
              />
            </label>
    
            <div style={{
                display: 'flex',
                flexDirection: 'column', 
                alignItems: 'center'    
                }}>
                    
                <button className='button-style' type="submit">Agregar Herramienta</button>
    
                </div>
        </form>
        </div>
    );


}

export default AddExistingToolForm;