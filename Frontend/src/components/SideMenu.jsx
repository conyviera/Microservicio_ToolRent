import * as React from "react";
import { styled, useTheme } from "@mui/material/styles";
import { Box, Toolbar, AppBar as MuiAppBar, List, Divider, IconButton, ListItemButton, ListItemIcon, ListItemText } from '@mui/material';
import MuiDrawer from "@mui/material/Drawer";
import { useNavigate, Outlet, useLocation } from "react-router-dom"; 

// --- Iconos ---
import MenuIcon from "@mui/icons-material/Menu"; 
import ChevronLeftIcon from "@mui/icons-material/ChevronLeft";
import PeopleAltIcon from "@mui/icons-material/PeopleAlt";
import ConstructionOutlinedIcon from "@mui/icons-material/ConstructionOutlined";
import HomeIcon from "@mui/icons-material/Home";
import CreditScoreIcon from "@mui/icons-material/CreditScore";
import AccountCircleIcon from "@mui/icons-material/AccountCircle";
import InventoryIcon from '@mui/icons-material/Inventory';
import logo from '../image/logo.png'; 
import { p } from "framer-motion/client";


const drawerWidth = 180;

const openedMixin = (theme) => ({
  width: drawerWidth,
  transition: theme.transitions.create("width", {
    easing: theme.transitions.easing.sharp,
    duration: theme.transitions.duration.enteringScreen,
  }),
  overflowX: "hidden",
});

const closedMixin = (theme) => ({
  transition: theme.transitions.create("width", {
    easing: theme.transitions.easing.sharp,
    duration: theme.transitions.duration.leavingScreen,
  }),
  overflowX: "hidden",
  width: `calc(${theme.spacing(7)} + 1px)`,
  [theme.breakpoints.up("sm")]: {
    width: `calc(${theme.spacing(8)} + 1px)`,
  },
});

const DrawerHeader = styled("div")(({ theme }) => ({
  display: "flex",
  alignItems: "center",
  justifyContent: "center",
  padding: theme.spacing(1, 1),
  ...theme.mixins.toolbar,
}));

const Drawer = styled(MuiDrawer, {
  shouldForwardProp: (prop) => prop !== "open",
})(({ theme, open }) => ({
  width: drawerWidth,
  flexShrink: 0,
  whiteSpace: "nowrap",
  boxSizing: "border-box",
  backgroundColor: '#EAEAD1',

  ...(open && {
    ...openedMixin(theme),
    "& .MuiDrawer-paper": {
      ...openedMixin(theme),
      backgroundColor: '#EAEAD1',
      overflow: 'hidden',         
      backdropFilter: "none",  
      WebkitBackdropFilter: "none",
      boxShadow: "none",
      border: "none",
    },
  }),
  ...(!open && {
    ...closedMixin(theme),
    "& .MuiDrawer-paper": {
      ...closedMixin(theme),
      backgroundColor: '#EAEAD1',
      overflow: 'hidden',          
      backdropFilter: "none",
      WebkitBackdropFilter: "none",
      boxShadow: "none",
      border: "none",
    },
  }),
}));

const selectedStyle = (theme) => ({
  backgroundColor: '#4E7D10', 
  borderRadius: '17px',
  width: 'auto',
  '&, &:hover': {
    backgroundColor: '#4E7D10', 
    filter: 'drop-shadow(0 0 10px #4E7D10)',
  },
  '& .MuiListItemIcon-root': {
    color: 'white',
  },
  '& .MuiListItemText-root': {
    color: 'white',
  },
});


export default function MainLayout() {
  const theme = useTheme();
  const navigate = useNavigate();
  const location = useLocation(); 
  const [open, setOpen] = React.useState(true);

  const handleToggleDrawer = () => {
    setOpen(!open);
  };

  return (
    
    <Box sx={{ display: "flex", minHeight: '100vh', bgcolor: '#a4be5c3a' }}>
      
      
      <Drawer variant="permanent" open={open}>
        <DrawerHeader>
          {open && (
            <Box
              component="img"
              sx={{ height: 100 }} 
              alt="Logo"
              src={logo}
            />
          )}
        </DrawerHeader>

        <List sx={{ p: 1.5 }}>
          <ListItemButton 
            onClick={() => navigate("/")}
            sx={location.pathname === "/" ? selectedStyle(theme) : {}
          }
          >
            <ListItemIcon><HomeIcon /></ListItemIcon>
            <ListItemText primary="Home" sx={{ opacity: open ? 1 : 0 }} />
          </ListItemButton>

          <ListItemButton 
            onClick={() => navigate("/CustomerManagementPage")}
            sx={location.pathname === "/CustomerManagementPage" ? selectedStyle(theme) : {}}
          >
            <ListItemIcon><PeopleAltIcon /></ListItemIcon>
            <ListItemText primary="Clientes" sx={{ opacity: open ? 1 : 0 }} />
          </ListItemButton>

          <ListItemButton 
            onClick={() => navigate("/ToolInventoryPage")}
            sx={location.pathname === "/ToolInventoryPage" ? selectedStyle(theme) : {}}
          >
            <ListItemIcon><ConstructionOutlinedIcon /></ListItemIcon>
            <ListItemText primary="Inventario" sx={{ opacity: open ? 1 : 0 }} />
          </ListItemButton>
          
          <ListItemButton 
            onClick={() => navigate("/LoanPage")}
            sx={location.pathname ==="/LoanPage" ? selectedStyle(theme) : {}}
          >
            <ListItemIcon><CreditScoreIcon /></ListItemIcon>
            <ListItemText primary="Prestamos" sx={{ opacity: open ? 1 : 0 }} />
          </ListItemButton>

          <ListItemButton 
            onClick={() => navigate("/Kardex")}
            sx={location.pathname ==="/Kardex" ? selectedStyle(theme) : {}}
          >
            <ListItemIcon><InventoryIcon /></ListItemIcon>
            <ListItemText primary="kardex" sx={{ opacity: open ? 1 : 0 }} />
          </ListItemButton>
        </List>
      </Drawer>

      <Box 
        component="main" 
        sx={{ 
          flexGrow: 1, 
          p: 0,  
          boxSizing: 'border-box', 
          backgroundColor: "#EAEAD1",
          minWidth: 0
        }}
      >
        
        <Box sx={{
          width: '100%',
          height: '100%',
          bgcolor: 'background.paper', 
          borderRadius: '20px', 
          overflow: 'hidden', 
          display: 'flex',
          flexDirection: 'column' 
        }}>

          <MuiAppBar 
            position="static" 
            elevation={0} 
            sx={{
              backgroundColor: "rgba(255, 255, 255, 0.7)",
              backdropFilter: "blur(10px)",
              WebkitBackdropFilter: "blur(10px)",
              borderBottom: "1px solid rgba(0, 0, 0, 0.12)",
              color: "#000" 
            }}
          >
            <Toolbar>
              <IconButton
                color="inherit"
                aria-label="toggle drawer"
                onClick={handleToggleDrawer}
                edge="start"
                sx={{ marginRight: 2 }} 
              >
                {open ? <ChevronLeftIcon /> : <MenuIcon />}
              </IconButton>
            

              <Box sx={{ flexGrow: 1 }} />

              <IconButton color="inherit" aria-label="Login">
                <AccountCircleIcon />
              </IconButton>
            </Toolbar>
          </MuiAppBar>

          <Box 
            sx={{ 
              flexGrow: 1, 
              p: 3, 
              overflowY: 'auto' 
              
            }}
          >
            <Outlet /> 
          </Box>
        </Box>
      </Box>
    </Box>
  );
}