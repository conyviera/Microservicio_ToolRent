import React, { useState } from 'react';
import toolService from '../../services/tool.services';
import Logo from '../../image/logo.png';

function AddToolForm({onToolAdded}) {
  const [name, setName] = useState('');
  const [category, setCategory] = useState('');
  const [replacementValue, setReplacementValue]= useState('');
  const [dailyRate, setDailyRate]=useState('');
  const [debtRate, setDebtRate]= useState(''); 

  const [amount, setAmount]= useState(''); 
  const clearForm = () => {
    setName('');
    setCategory('');
    setReplacementValue('');
    setDailyRate('');
    setDebtRate('');
    };


  const handleSubmit = async (event) => {
    event.preventDefault(); 
    const toolData= 
      { name: name,
      category: category,
      replacementValue: parseInt(replacementValue,10),
      dailyRate:  parseInt(dailyRate,10),
      debtRate:  parseInt(debtRate,10),
      amount : parseInt(amount,10)
     };
  
    try {
    const response = await toolService.createLotTool(toolData);
    console.log('Herramienta creada:', response.data);
    alert('¡Herramienta agregada con éxito!'); 
    onToolAdded();
    clearForm();
    
  } catch (error) {
    console.error('Error al agregar la herramienta:', error);

      if (error.response && error.response.data) {
          alert(error.response.data); 
      } else {
          alert('Hubo un error de conexión al agregar La herramienta.');
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

         <h3 className='title-input'>Agregar Nueva herramienta</h3>
        <label>
          Nombre:
          <input
            className= "input-style"
            type="text"
            value={name}
            onChange={(e) => setName(e.target.value)          }
          />
        </label>
        <label>
          Categoria:
          <input
            className= "input-style"
            type="text"
            value={category}
            onChange={(e) => setCategory(e.target.value)          }
          />
        </label>
        <label>
          Valor:
          <input
            className= "input-style"
            type="number"
            value={replacementValue}
            onChange={(e) => {const value = e.target.value;
              if (value === '' || Number(value) >= 0) {
                setReplacementValue(e.target.value);
              }}}
          />
        </label>
        <label>
          Valor de arriendo por dia:
          <input
            className= "input-style"
            type="number"
            value={dailyRate}
            onChange={(e) => {const value = e.target.value;
              if (value === '' || Number(value) >= 0) {
                 setDailyRate(e.target.value);
                }}}
          />
        </label>
        <label>
          Valor de arriendo por dia de atraso:
          <input
            className= "input-style"
            type="number"
            value={debtRate}
            onChange={(e) => {const value = e.target.value;
              if (value === '' || Number(value) >= 0) {
                setDebtRate(e.target.value);
                }}}
          />
        </label>
        <label>
          Cantidad a agregar:
          <input
            className= "input-style"
            type="number"
            value={amount}
            onChange={(e) => {const value = e.target.value;
              if (value === '' || Number(value) >= 0) {
                setAmount(e.target.value);
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

export default AddToolForm;