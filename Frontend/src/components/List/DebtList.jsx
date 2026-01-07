import React, { useState, useEffect } from "react";
import { IconButton, Pagination, Stack } from "@mui/material"; 
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';
import { styled } from '@mui/material/styles';
import MuiTableCell from '@mui/material/TableCell';
import debtsServices from '../../services/debts.services.js';
import { useParams } from "react-router-dom";
import BasicModal from '../Modal.jsx';
import AttachMoneyIcon from '@mui/icons-material/AttachMoney';
import DebtPaidForm from '../Form/DebtPaidForm.jsx';

const StyledTableHead = styled(TableHead)(({ theme }) => ({
  backgroundColor: '#4E7D10',
  
}));


const StyledHeaderCell = styled(MuiTableCell)(({ theme }) => ({
  fontWeight: 'bold',
  color: '#ffffffff', 
  textAlign: 'center',
}));

const StyledBodyRow = styled(TableRow)(({ theme }) => ({
  '&:hover': {
    backgroundColor: theme.palette.action.hover, 
    cursor: 'pointer',
  },

  '&:last-child td, &:last-child th': {
    border: 0,
  },
}));


const DebtList = ({ }) => {

    const { id } = useParams();
    const idLoan = id;

    const [debt, setDebt] = useState([]);
    const [reload, setReload] = useState(false); 

    useEffect(() => {
        const fetchDebt = async () => {
            try {
                const response = await debtsServices.getDebtsByLoanId(idLoan);
                setDebt(response.data || []);
            } catch (error) {
                console.error("Error al obtener las deudas", error);
                setDebt([]);
            }   
        };
        fetchDebt();
    }, [idLoan, reload]);

    const handleDebtPaid = () => {
      setReload(!reload);
    };

    /*-------------------------Paginación-------------------------*/
    const ITEM_PER_PAGE = 5; 
    const [currentPage, setCurrentPage] = useState(1);
    const handlePageChange = (_event, value) => setCurrentPage(value);

    const totalPages = Math.max(1, Math.ceil(debt.length / ITEM_PER_PAGE));
    
    const indexOfLastItem = currentPage * ITEM_PER_PAGE;
    const indexOfFirstItem = indexOfLastItem - ITEM_PER_PAGE;
    const itemsCurrentPage = debt.slice(indexOfFirstItem, indexOfLastItem);
  

  return (
    <div className="table-container">
        <div>
            <h4>Deudas Asociadas al Préstamo ID: {idLoan}</h4>
            {debt.length === 0?
                (<p>No hay deudas asociadas a este préstamo.</p>):
                (<p>Total de deudas: {debt.length}</p>)
            }
        </div>
      <TableContainer 
      component={Paper}
      sx={{ 
        borderRadius: '10px', 
        overflow: 'hidden'   
      }}>
      <Table sx={{ minWidth: 800}} aria-label="simple table">
        <StyledTableHead>
          <TableRow>
            <StyledHeaderCell align="center">ID</StyledHeaderCell>
            <StyledHeaderCell align="center">Tipo</StyledHeaderCell>
            <StyledHeaderCell align="center">Monto</StyledHeaderCell>
            <StyledHeaderCell align="center">Fecha creación</StyledHeaderCell>
            <StyledHeaderCell align="center">Fecha pago</StyledHeaderCell>
            <StyledHeaderCell align="center">Estado</StyledHeaderCell>
            <StyledHeaderCell align="center">Herramientas</StyledHeaderCell>
          </TableRow>
        </StyledTableHead>
        <TableBody>
          {itemsCurrentPage.map((debtUnitary) => (
            <StyledBodyRow
              key={debtUnitary.idDebts} 
            >
              <MuiTableCell align="center">{debtUnitary.idDebts}</MuiTableCell>
              <MuiTableCell align="center">{debtUnitary.type}</MuiTableCell>
              <MuiTableCell align="center">{debtUnitary.amount}</MuiTableCell>
              <MuiTableCell align="center">{debtUnitary.creationDate}</MuiTableCell>
              <MuiTableCell align="center">
                {debtUnitary.paymentDate ? (
                  debtUnitary.paymentDate
                ) : ( 
                  debtUnitary.amount === 0 ? (
                    'N/A'
                  ) : (
                    <BasicModal
                      button={
                        <IconButton
                          onClick={() => setOpen(true)} 
                          sx={{
                            color: '#4E7D10',
                            '&:hover': { filter: 'drop-shadow(0 0 10px #4E7D10)' },
                          }}
                        >
                          <AttachMoneyIcon />
                        </IconButton>
                      }
                    >
                      <DebtPaidForm debt={debtUnitary} onDebtPaid={handleDebtPaid} />
                    </BasicModal>
                  )
                )}
              </MuiTableCell>
              <MuiTableCell align="center">{debtUnitary.status}</MuiTableCell>
              <MuiTableCell align="center">
                {(Array.from( 
                    new Set(    
                      (debtUnitary.tool || []) 
                        .map(h => h?.typeTool?.name) 
                        .filter(Boolean)                   
                    ) 
                  ).join(', ') 
                ) 
                || 'Sin herramientas' 
                }
              </MuiTableCell>

            </StyledBodyRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
    {debt.length > 0 && (
        <Stack spacing={2} sx={{ mt: 2, alignItems: 'center' }}>
            <Pagination 
                count={totalPages} 
                page={currentPage} 
                onChange={handlePageChange} 
                color="primary" 
            />
        </Stack>
    )}
    </div>
  );
}
export default DebtList;
