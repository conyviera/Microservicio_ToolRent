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
            setLoanDetails(response.data);

            const initialStates = {};
            response.data.tool.forEach(tool => {
                initialStates[tool.idTool] = 'GOOD';
            });
            setToolStates(initialStates);

        } catch (err) {
            if (err.response && err.response.data && err.response.data.message) {
                setError(err.response.data.message);
            } else {
                setError('No se pudo encontrar el préstamo con ese ID. Verifica la conexión o el ID ingresado.');
            }
            console.error("Error al buscar el préstamo:", err);
        }
        setIsLoading(false);
    };

    const handleStateChange = (toolId, newState) => {
        setToolStates(prevStates => ({
            ...prevStates,
            [toolId]: newState
        }));
    };
    const handleSubmitReturn = async (event) => {
        event.preventDefault();
        setIsLoading(true);
        setError('');

        const toolStatesArray = Object.keys(toolStates).map(toolId => ({
            toolId: parseInt(toolId, 10),
            state: toolStates[toolId]
        }));

        const payload = {
            toolStates: toolStatesArray
        };

        console.log(">>>>> PAYLOAD DE DEVOLUCIÓN ENVIADO AL BACKEND: ", payload);

        try {
            await loanService.returnLoan(loanId, payload);
            alert('¡Préstamo devuelto con éxito!');
            setLoanId('');
            setLoanDetails(null);
            setToolStates({});
            onReturnLoan();
        } catch (err) {
            if (err.response && err.response.data && err.response.data.message) {
            
                setError(err.response.data.message);
            } else {
                setError('Hubo un error al procesar la devolución. Por favor, inténtalo de nuevo.');
            }
            console.error("Error al devolver el préstamo:", err);
            
        }
        setIsLoading(false);
    };

    return (
        <div style={{ overflowY: 'auto', maxHeight: '100vh', padding: '20px' }}>
            <h2>Registrar Devolución de Préstamo</h2>

            {!loanDetails && (
                <div>
                    <label>
                        ID del Préstamo:
                        <input
                            className= "input-style"
                            type="number"
                            value={loanId}
                            onChange={(e) => setLoanId(e.target.value)}
                            placeholder="Ingresa ID del préstamo"
                        />
                    </label>
                    
                    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', marginTop: '20px' }}>
                        <br/>
                        <button  className='button-style' onClick={handleFetchLoan} disabled={isLoading}>
                            {isLoading ? 'Buscando...' : 'Buscar Préstamo'}
                        </button>
                    </div>

                </div>
            )}

            {error && <p style={{ color: 'red' }}>{error}</p>}

            {loanDetails && (
                <form onSubmit={handleSubmitReturn}>
                    <h3>Detalles del Préstamo #{loanDetails.idLoan}</h3>
                    
                    {loanDetails.customer && <p>Cliente: {loanDetails.customer.name}</p>}
                    
                    <h4>Estado de Herramientas Devueltas:</h4>
                    
                    {loanDetails.tool.map(tool => (
                        <div key={tool.idTool}>
                            <label>{tool.typeTool.name} (ID: {tool.idTool})</label>
                            <select
                                className= "input-style"
                                value={toolStates[tool.idTool] || ''}
                                onChange={(e) => handleStateChange(tool.idTool, e.target.value)}
                            >
                                <option value="GOOD">Buen Estado</option>
                                <option value="DAMAGED">Dañado</option>
                            </select>
                        </div>
                    ))}
                    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', marginTop: '20px' }}>
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