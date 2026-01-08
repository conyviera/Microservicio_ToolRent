import React, { useState } from 'react';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';
import { styled } from '@mui/material/styles';
import MuiTableCell from '@mui/material/TableCell';
import { useNavigate } from "react-router-dom";
import EditIcon from '@mui/icons-material/Edit'
import BuildIcon from '@mui/icons-material/Build';
import { Box, IconButton } from '@mui/material';
import AddIcon from '@mui/icons-material/Add';

import BasicModal from '../Modal.jsx';
import AddExistingToolForm from "../Form/AddExistingToolForm.jsx";
import UpdateTypeToolForm from '../Form/UpdateTypeToolForm.jsx';

// Estilos...
const StyledTableHead = styled(TableHead)(({ theme }) => ({ backgroundColor: '#4E7D10' }));
const StyledHeaderCell = styled(MuiTableCell)(({ theme }) => ({ fontWeight: 'bold', color: '#ffffff', textAlign: 'center' }));
const StyledBodyRow = styled(TableRow)(({ theme }) => ({
    '&:hover': { backgroundColor: theme.palette.action.hover, cursor: 'pointer' },
    '&:last-child td, &:last-child th': { border: 0 },
}));

const TypeToolList = ({ typeTool, onRefreshList }) => {
    const navigate = useNavigate();

    const handleRowClick = (id) => {
        navigate(`/typeTool/${id}`);
    }

    const handleFormSubmitSuccess = () => {
        // Esto recarga la lista llamando al padre
        if (onRefreshList) onRefreshList();
    };

    return (
        <div className="table-container">
            <TableContainer component={Paper} sx={{ borderRadius: '10px', overflow: 'hidden' }}>
                <Table sx={{ minWidth: 800 }} aria-label="simple table">
                    <StyledTableHead>
                        <TableRow>
                            <StyledHeaderCell align="center">ID</StyledHeaderCell>
                            <StyledHeaderCell align="center">Nombre</StyledHeaderCell>
                            <StyledHeaderCell align="center">Categoria</StyledHeaderCell>
                            <StyledHeaderCell align="center">Valor Reposición</StyledHeaderCell>
                            <StyledHeaderCell align="center">Tarifa Diaria</StyledHeaderCell>
                            <StyledHeaderCell align="center">Tarifa Atraso</StyledHeaderCell>
                            <StyledHeaderCell align="center">Stock</StyledHeaderCell>
                            <StyledHeaderCell align="center">Acción</StyledHeaderCell>
                        </TableRow>
                    </StyledTableHead>
                    <TableBody>
                        {typeTool.map((tool) => (
                            <StyledBodyRow key={tool.idTypeTool}>
                                <MuiTableCell align="center">{tool.idTypeTool}</MuiTableCell>
                                <MuiTableCell align="center">{tool.name}</MuiTableCell>
                                <MuiTableCell align="center">{tool.category}</MuiTableCell>
                                <MuiTableCell align="center">{tool.replacementValue}</MuiTableCell>
                                <MuiTableCell align="center">{tool.dailyRate}</MuiTableCell>
                                <MuiTableCell align="center">{tool.debtRate}</MuiTableCell>
                                {/* Este valor vendrá actualizado si arreglas el Backend */}
                                <MuiTableCell align="center" style={{ fontWeight: 'bold' }}>{tool.stock}</MuiTableCell> 
                                <MuiTableCell align="center">
                                    <Box display="flex" justifyContent="center" alignItems="center">
                                        
                                        {/* Botón AGREGAR STOCK (+) */}
                                        <div onClick={(e) => e.stopPropagation()}>
                                            <BasicModal
                                                button={
                                                    <IconButton sx={{ color: '#4E7D10' }}>
                                                        <AddIcon />
                                                    </IconButton>
                                                }
                                            >
                                                {/* Pasamos la herramienta actual al formulario */}
                                                <AddExistingToolForm
                                                    typeTool={tool}
                                                    onToolAdded={handleFormSubmitSuccess} 
                                                />
                                            </BasicModal>
                                        </div>

                                        {/* Botón EDITAR */}
                                        <div onClick={(e) => e.stopPropagation()}>
                                            <BasicModal
                                                button={
                                                    <IconButton sx={{ color: '#4E7D10' }}>
                                                        <EditIcon />
                                                    </IconButton>
                                                }
                                            >
                                                <UpdateTypeToolForm 
                                                    idTypeTool={tool.idTypeTool} 
                                                    onUpdate={handleFormSubmitSuccess} 
                                                />
                                            </BasicModal>
                                        </div>

                                        {/* Botón VER DETALLE */}
                                        <IconButton
                                            onClick={() => handleRowClick(tool.idTypeTool)}
                                            sx={{ color: '#4E7D10' }}
                                        >
                                            <BuildIcon />
                                        </IconButton>

                                    </Box>
                                </MuiTableCell>
                            </StyledBodyRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
        </div>
    );
}
export default TypeToolList;