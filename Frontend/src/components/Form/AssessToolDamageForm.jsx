import React, {useState} from 'react';
import debtsServices from '../../services/debts.services.js';

function AssessToolDamageForm({ debt, onAssessmentComplete }){ 
    const [outcome, setOutcome]= useState('');
    const [damageCharge, setDamageCharge]= useState('');

    if (!debt) return null;

    const handleSubmit= async (e)=>{
        e.preventDefault();
        
        // Validación solo para daño menor
        if (outcome === 'MINOR_DAMAGE' && (damageCharge === '' || parseInt(damageCharge) < 0)) {
            alert("Debe ingresar un costo de reparación válido.");
            return;
        }

        const isConfirmed = window.confirm("¿Confirma la evaluación del daño?");  
        if (isConfirmed) {
            try {
                let finalCharge = 0;
                // Solo enviamos costo si es daño menor. Si es irreparable, mandamos 0 (el backend lo calcula)
                if (outcome === 'MINOR_DAMAGE' && damageCharge !== '') {
                    finalCharge = parseInt(damageCharge);
                }

                const data = {
                    outcome,
                    cost: finalCharge
                };

                await debtsServices.assessDebt(debt.idDebts, data);
                
                alert("¡Evaluación registrada exitosamente!");
                onAssessmentComplete();
            } catch (error) {
                console.error("Error al registrar evaluación", error);
                alert("Error al registrar la evaluación.");
            }   
        }
    };

    return( 
        <form onSubmit={handleSubmit}>
            <h3>Evaluar Daño - Deuda #{debt.idDebts}</h3>
            <div>
                <label>Resultado de la evaluación: </label>
                <select className="input-style" value={outcome} onChange={(e)=> setOutcome(e.target.value)} required>
                    <option value="">Seleccione una opción</option>
                    <option value="IRREPARABLE">Irreparable (Cobro automático reposición)</option>
                    <option value="MINOR_DAMAGE">Daño Menor (Ingresar costo reparación)</option>
                </select>
            </div>
            
            {/* Solo mostramos el input si es Daño Menor */}
            {outcome === 'MINOR_DAMAGE' && (
                <div>
                    <label>Costo de Reparación ($): </label>
                    <input 
                        type="number" 
                        value={damageCharge}
                        className="input-style"
                        onChange={(e) => setDamageCharge(e.target.value)}
                        min="0"
                        placeholder="Ingrese costo reparación"
                        required
                    />
                </div>
            )}
            
            {outcome === 'IRREPARABLE' && (
                 <p style={{color: '#666', fontSize: '0.9em', fontStyle: 'italic'}}>
                    * El sistema calculará automáticamente el valor de reposición según el catálogo.
                 </p>
            )}

            <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', marginTop: '20px' }}>
                <button className='button-style' type="submit">Registrar Evaluación</button>
            </div>
        </form>
    );
}   
export default AssessToolDamageForm;