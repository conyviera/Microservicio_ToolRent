import React, { useState, useEffect } from 'react'; 
import loanService from '../../services/loan.services';
import toolServices from '../../services/tool.services';
import Logo from '../../image/logo.png';

// Importa los componentes de MUI
import Box from '@mui/material/Box';
import OutlinedInput from '@mui/material/OutlinedInput';
import InputLabel from '@mui/material/InputLabel';
import MenuItem from '@mui/material/MenuItem';
import FormControl from '@mui/material/FormControl';
import Select from '@mui/material/Select';
import Chip from '@mui/material/Chip';
import Button from '@mui/material/Button';

const ITEM_HEIGHT = 48;
const ITEM_PADDING_TOP = 8;
const MenuProps = {
  PaperProps: {
    style: {
      maxHeight: ITEM_HEIGHT * 4.5 + ITEM_PADDING_TOP,
      width: 250,
    },
  },
};

function AddLoanForm({ onLoanAdded }) {
  const [typeToolIds, setTypeToolIds] = useState([]); 
  const [typeTool, setTypeTool] = useState([]); 
  
  const [customerId, setCustomerId] = useState('');
  const [deliveryDate, setDeliveryDate] = useState('');
  const [returnDate, setReturnDate] = useState('');

  const [calculatedAmount, setCalculatedAmount] = useState(null);
  const [isCalculating, setIsCalculating] = useState(false);

  useEffect(() => {
    const fetchTypeTool = async () => {
      try {
        const response = await toolServices.getAllTypeTools();
        setTypeTool(response.data || []);
      } catch (error) {
        console.error("Error al obtener los tipos de herramientas", error);
        setTypeTool([]);
      }
    };
    fetchTypeTool();
  }, []);

  useEffect(() => {
    setCalculatedAmount(null);
  }, [typeToolIds, deliveryDate, returnDate]);

  const clearForm = () => {
    setTypeToolIds([]);
    setCustomerId('');
    setDeliveryDate('');
    setReturnDate('');
    setCalculatedAmount(null);
  };

  // Funcion que calcula el monto del prestamo 
  const handleCalculateTotal = async () => {
    if (typeToolIds.length === 0 || !deliveryDate || !returnDate) {
        alert("Selecciona herramientas y fechas para calcular.");
        return;
    }

    setIsCalculating(true);
    const payload = {
        typeToolIds: typeToolIds,
        deliveryDate: deliveryDate,
        returnDate: returnDate
    };

    try {
        const response = await loanService.rentalAmount(payload);
        setCalculatedAmount(response.data);
    } catch (error) {
        console.error("Error calculando monto:", error);
        alert("Error al calcular el monto.");
    } finally {
        setIsCalculating(false);
    }
  };

  const handleSelectChange = (event) => {
    const { target: { value } } = event;
    setTypeToolIds(typeof value === 'string' ? value.split(',') : value);
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    if (typeToolIds.length === 0) {
      alert("Debes seleccionar al menos una herramienta");
      return;
    }

    const loanData = {
      typeToolIds: typeToolIds,
      customerId: parseInt(customerId, 10),
      deliveryDate: deliveryDate,
      returnDate: returnDate
    };
    
    try {
      await loanService.createLoan(loanData);
      alert('¡Préstamo agregado con éxito!');

      if(onLoanAdded) onLoanAdded();
      clearForm();

    } catch (error) {
      console.error('Error al crear el préstamo:', error);

      if (error.response && error.response.data) {
          alert(error.response.data); 
      } else {
          alert('Hubo un error de conexión al agregar el préstamo.');
      }
    }
  };

  // Helper para obtener el nombre de la herramienta por su ID
  const getToolNameById = (id) => {
    const tool = typeTool.find(t => t.idTypeTool === id);
    return tool ? tool.name : id;
  };

  // Helper para formatear dinero (CLP o USD según tu preferencia)
  const formatMoney = (amount) => {
    return new Intl.NumberFormat('es-CL', { style: 'currency', currency: 'CLP' }).format(amount);
  };

  return (
    // CORRECCIÓN SCROLL: Aseguramos que el contenedor permita scroll si es muy alto
    <div className="form-container" style={{ overflowY: 'auto', maxHeight: '100vh', padding: '20px' }}>
      <form className="form-data" onSubmit={handleSubmit}>
        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
          <img src={Logo} alt="Logo" style={{ height: '90px' }} />
        </div>
        
        <h3 className='title-input'>Agregar Nuevo Préstamo</h3>
        
        <label>
          Herramientas:
          <FormControl sx={{ m: 1, width: 300 }}>
            <InputLabel id="demo-multiple-chip-label">Herramientas</InputLabel>
            <Select
              labelId="demo-multiple-chip-label"
              id="demo-multiple-chip"
              multiple
              value={typeToolIds}
              onChange={handleSelectChange}
              input={<OutlinedInput id="select-multiple-chip" label="Herramientas" />}
              
              
              renderValue={(selected) => (
                <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5 }}>
                  {selected.map((value) => (
                    <Chip key={value} label={getToolNameById(value)} />
                  ))}
                </Box>
              )}
              MenuProps={MenuProps}
            >
              {typeTool.map((type) => (
                <MenuItem
                  key={type.idTypeTool}
                  value={type.idTypeTool} 
                >
                  {`${type.idTypeTool} - ${type.name}`}
                </MenuItem>
              ))}
            </Select>
          </FormControl>
        </label>
        
        <br />

        <label>
          ID del Cliente:
          <input
            className="input-style"
            placeholder="Ej: 1"
            type="number"
            value={customerId}
            onChange={(e) => {
                const value = e.target.value;
              if (value === '' || Number(value) >= 0) {
                setCustomerId(value);
              }
            }}
          />
        </label>
        
        <label>
          Fecha de Préstamo:
          <input
            className="input-style"
            type="date"
            value={deliveryDate}
            onChange={(e) => setDeliveryDate(e.target.value)}
          />
        </label>
        
        <label>
          Fecha de Devolución:
          <input
            className="input-style"
            type="date"
            value={returnDate}
            onChange={(e) => setReturnDate(e.target.value)}
          />
        </label>

      <div>
        {calculatedAmount !== null && (
          <div style={{ textAlign: 'center', marginBottom: '15px', marginTop: '20px' }}>
            <strong style={{ fontSize: '1.2em', color: '#000102ff' }}>
              Total Estimado: {formatMoney(calculatedAmount)}
              </strong>
          </div>
        )}
        
        <div style={{ 
                display: 'flex', 
                flexDirection: 'row',
                justifyContent: 'center',
                gap: '20px',
                marginTop: '20px'
                }}>
                
                <button 
                    className='button-style' 
                    type="button"
                    onClick={handleCalculateTotal} 
                    disabled={isCalculating || typeToolIds.length === 0 || !deliveryDate || !returnDate}
                >
                    {isCalculating ? 'Calculando...' : 'Cotizar Valor'}
                </button>

                <button className='button-style' type="submit">
                    Agregar Préstamo
                </button>
                
            </div>
        </div>
      </form>
    </div>
  );
}

export default AddLoanForm;