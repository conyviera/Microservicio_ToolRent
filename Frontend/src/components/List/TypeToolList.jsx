import React, {useState} from 'react';
import { Tab } from "@mui/material";
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';
import { styled } from '@mui/material/styles';
import MuiTableCell from '@mui/material/TableCell';
import { useNavigate} from "react-router-dom";
import EditIcon from '@mui/icons-material/Edit'
import BuildIcon from '@mui/icons-material/Build';
import { Button, Box, IconButton } from '@mui/material';
import AddIcon from '@mui/icons-material/Add';

import BasicModal from '../Modal.jsx';
import ReturnLoanForm from '../Form/ReturnLoanForm.jsx';
import AddExistingToolForm from "../Form/AddExistingToolForm.jsx";
import UpdateTypeToolForm from '../Form/UpdateTypeToolForm.jsx';

// Estilo para el Encabezado
const StyledTableHead = styled(TableHead)(({ theme }) => ({
  backgroundColor: '#4E7D10', // Un gris claro del tema
  
}));

// Estilo para las Celdas del Encabezado
const StyledHeaderCell = styled(MuiTableCell)(({ theme }) => ({
  fontWeight: 'bold',
  color: '#ffffffff', // Color de texto primario del tema
  textAlign: 'center',
}));

// Estilo para las Filas del Cuerpo
const StyledBodyRow = styled(TableRow)(({ theme }) => ({
  '&:hover': {
    backgroundColor: theme.palette.action.hover, 
    cursor: 'pointer',
  },
  
  '&:last-child td, &:last-child th': {
    border: 0,
  },
}));


const TypeToolList = ({ typeTool, onRefreshList}) => {

  const navigate= useNavigate();

  
  const [addModalOpen, setAddModalOpen] = useState(false);
  const [editModalOpen, setEditModalOpen] = useState(false);
  const [selectedTool, setSelectedTool] = useState(null);

  const handleRowClick=(id) =>{
    navigate(`/typeTool/${id}`);
  }

  const handleOpenModal = (tool) => {
    setSelectedTool(tool); 
    setAddModalOpen(true);         
  };


  const handleFormSubmitSuccess = () => {
    onRefreshList();      
  };
  
  return (
    <div className="table-container">
      <TableContainer 
      component={Paper}
      sx={{ 
        borderRadius: '10px', 
        overflow: 'hidden'   
      }}>
      <Table sx={{ minWidth: 800}} aria-label="simple table">
        <StyledTableHead>
          <TableRow>
            <StyledHeaderCell key= "head-id" align="center">ID</StyledHeaderCell>
            <StyledHeaderCell key= "head-name" align="center">Nombre</StyledHeaderCell>
            <StyledHeaderCell key= "head-category" align="center">Categoria</StyledHeaderCell>
            <StyledHeaderCell key= "head-replacementValue" align="center">Valor de reposición</StyledHeaderCell>
            <StyledHeaderCell key= "head-dailyRate" align="center">Tarifa diaria</StyledHeaderCell>
            <StyledHeaderCell key= "head-debtRate" align="center">Tarifa por día de atraso</StyledHeaderCell>
            <StyledHeaderCell key= "head-stock" align="center">Disponibles</StyledHeaderCell>
            <StyledHeaderCell key= "head-button" align="center"> Acción</StyledHeaderCell>
            
          </TableRow>
        </StyledTableHead>
        <TableBody>
          {typeTool.map((tool) => (
            <StyledBodyRow
              key={tool.idTypeTool} 
            >
              <MuiTableCell align="center">{tool.idTypeTool}</MuiTableCell>
              <MuiTableCell align="center">{tool.name}</MuiTableCell>
              <MuiTableCell align="center">{tool.category}</MuiTableCell>
              <MuiTableCell align="center">{tool.replacementValue}</MuiTableCell>
              <MuiTableCell align="center">{tool.dailyRate}</MuiTableCell>
              <MuiTableCell align="center">{tool.debtRate}</MuiTableCell>
              <MuiTableCell align="center">{tool.stock}</MuiTableCell>
              <MuiTableCell align="center">
                <Box 
                  display="flex"
                  justifyContent="center"
                  alignItems="center"
                >
                  <BasicModal
                      open={open}                
                      button={
                        <IconButton
                          
                          onClick={() => handleOpenModal(tool)} 
                          sx={{
                            color: '#4E7D10',
                            '&:hover': {
                              filter: 'drop-shadow(0 0 10px #4E7D10)',
                            },
                          }}
                        >
                          <AddIcon/> 
                        </IconButton>
                      }
                    >
                      <AddExistingToolForm 
                      typeTool={tool} 
                      onToolAdded={handleFormSubmitSuccess}/>
                  </BasicModal>
                  <BasicModal
                    button={
                      <IconButton
                        onClick={() => setOpen(true)}
                        sx={{
                          color: '#4E7D10',
                          '&:hover': {
                            filter: 'drop-shadow(0 0 10px #4E7D10)',
                          },
                        }}
                      >
                        <EditIcon />
                      </IconButton>
                    }
                  >
                    <UpdateTypeToolForm idTypeTool={tool.idTypeTool} onUpdate={onRefreshList} />
                  </BasicModal>
                  <IconButton
                    onClick={() => handleRowClick(tool.idTypeTool)}
                    sx={{
                      color: '#4E7D10',
                      '&:hover': {
                        filter: 'drop-shadow(0 0 10px #4E7D10)',
                      }
                    }}
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
