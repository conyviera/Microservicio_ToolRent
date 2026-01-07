import React, {useState} from 'react';
import loanService from '../../services/loan.services.js';

function DebtPaidForm({debt, onDebtPaid}){
    const [amountPaid, setAmountPaid]= useState('');

    if (!debt) {
        return null;
    }

    const handleSubmit= async (e)=>{
        e.preventDefault();
        const isConfirmed = window.confirm("Confirma que deseas pagar esta deuda?");
        
        if (isConfirmed) {
            try {
                await loanService.payDebt(debt.idDebts);
                alert("¡Deuda pagada con éxito!");
                onDebtPaid();
            } catch (error) {
                console.error("Error al pagar la deuda", error);
                alert("Error al pagar la deuda. Por favor, inténtalo de nuevo.");
            }
        }
    };
    return(
        <form onSubmit={handleSubmit}>
            <div>
                <label>Monto de la deuda: </label>
                <span>{debt.amount}</span>
            </div>
            <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', marginTop: '20px' }}>
                   <button className='button-style' type="submit">Pagar Deuda</button>             
            </div>
        </form>
    );
}
export default DebtPaidForm;