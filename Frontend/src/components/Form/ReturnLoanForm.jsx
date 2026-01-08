import React, { useState } from 'react';
import loanService from '../../services/loan.services'; 
import '../../App.css';

function ReturnLoanForm({onReturnLoan}) {
    const [loanId, setLoanId] = useState('');
    const [loanDetails, setLoanDetails] = useState(null);
    const [toolStates, setToolStates] = useState({});
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState('');

    const handleFetchLoan = async () => {
        if (!loanId) {
            setError('Por favor, ingresa un ID de préstamo.');
            return;
        }
        setIsLoading(true);
        setError('');
        setLoanDetails(null);
        try {
            const response = await loanService.getLoanById(loanId);
            const loanData = response.data;
            
            console.log("Datos del préstamo recibidos:", loanData); // 🔍 DEBUG

            const initialStates = {};
            if (loanData.tool) {
                loanData.tool.forEach((tool, index) => {
                    // Validamos que el ID exista
                    const id = tool.idTool;
                    if (!id) console.warn(`⚠️ Herramienta en índice ${index} no tiene idTool!`, tool);
                    
                    if (id) {
                        initialStates[id] = 'GOOD';
                    }
                });
            }
            setLoanDetails(loanData);
            setToolStates(initialStates);

        } catch (err) {
            if (err.response && err.response.data) {
                const msg = err.response.data.message || 'Error al buscar el préstamo.';
                setError(msg);
            } else {
                setError('No se pudo conectar con el servidor.');
            }
            console.error("Error al buscar el préstamo:", err);
        }
        setIsLoading(false);
    };

    const handleStateChange = (toolId, newState) => {
        console.log(`Cambiando estado de herramienta ${toolId} a ${newState}`); // 🔍 DEBUG
        setToolStates(prevStates => ({
            ...prevStates,
            [toolId]: newState
        }));
    };

    const handleSubmitReturn = async (event) => {
        event.preventDefault();
        setIsLoading(true);
        setError('');

        const payload = {
            toolStates: toolStates 
        };

        console.log("Enviando devolución:", payload);

        try {
            await loanService.returnLoan(loanId, payload);
            alert('¡Préstamo devuelto con éxito!');
            setLoanId('');
            setLoanDetails(null);
            setToolStates({});
            if(onReturnLoan) onReturnLoan();
        } catch (err) {
            console.error("Error al devolver:", err);
            const msg = err.response?.data?.message || 'Error al procesar la devolución.';
            setError(msg);
        }
        setIsLoading(false);
    };

    const renderToolName = (tool) => {
        if (tool.typeTool && tool.typeTool.name) return tool.typeTool.name;
        if (tool.typeTool && tool.typeTool.idTypeTool) return `Tipo ${tool.typeTool.idTypeTool}`;
        return `Herramienta desconocida`;
    };

    return (
        <div style={{ overflowY: 'auto', maxHeight: '100vh', padding: '20px' }}>
            <h2>Registrar Devolución de Préstamo</h2>

            {!loanDetails && (
                <div>
                    <label>
                        ID del Préstamo:
                        <input
                            className="input-style"
                            type="number"
                            value={loanId}
                            onChange={(e) => setLoanId(e.target.value)}
                            placeholder="Ingresa ID del préstamo"
                        />
                    </label>
                    <div style={{ marginTop: '20px', textAlign: 'center' }}>
                        <button className='button-style' onClick={handleFetchLoan} disabled={isLoading}>
                            {isLoading ? 'Buscando...' : 'Buscar Préstamo'}
                        </button>
                    </div>
                </div>
            )}

            {error && <p style={{ color: 'red', fontWeight: 'bold' }}>{error}</p>}

            {loanDetails && (
                <form onSubmit={handleSubmitReturn}>
                    <h3>Préstamo #{loanDetails.idLoan}</h3>
                    {loanDetails.customer && <p><strong>Cliente:</strong> {loanDetails.customer.name}</p>}
                    
                    <h4>Estado de las Herramientas:</h4>
                    
                    {loanDetails.tool && loanDetails.tool.map((tool, index) => {
                        // Si idTool es nulo, usamos el índice como fallback para que React no explote, 
                        // pero mostramos error visual.
                        const toolKey = tool.idTool || `err-${index}`; 
                        
                        return (
                            <div key={toolKey} style={{ marginBottom: '15px', padding: '10px', border: '1px solid #ddd', borderRadius: '5px' }}>
                                <label style={{display: 'block', marginBottom: '5px'}}>
                                    {renderToolName(tool)} <span style={{color: '#666'}}>ID: {tool.idTool || 'NULO'}</span>
                                </label>
                                
                                {tool.idTool ? (
                                    <select
                                        style={{ width: '100%', padding: '8px' }} // Estilo directo para evitar problemas de CSS
                                        value={toolStates[tool.idTool] || ''}
                                        onChange={(e) => handleStateChange(tool.idTool, e.target.value)}
                                    >
                                        <option value="GOOD">Buen Estado</option>
                                        <option value="DAMAGED">Dañado</option>
                                    </select>
                                ) : (
                                    <p style={{color: 'red'}}>Error: Esta herramienta no tiene ID válido.</p>
                                )}
                            </div>
                        );
                    })}

                    <div style={{ marginTop: '20px', textAlign: 'center' }}>
                        <button className='button-style' type="submit" disabled={isLoading}>
                            {isLoading ? 'Procesando...' : 'Confirmar Devolución'}
                        </button>       
                    </div>
                </form>
            )}
        </div>
    );
}

export default ReturnLoanForm;