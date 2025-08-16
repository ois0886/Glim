"use client";

import React from 'react';
import { ThemeProvider } from 'styled-components';
import GlobalStyles from '../styles/GlobalStyles';

interface ThemeProviderWrapperProps {
  children: React.ReactNode;
}

const ThemeProviderWrapper: React.FC<ThemeProviderWrapperProps> = ({ children }) => {
  const theme = {}; // Define your theme here if needed

  return (
    <ThemeProvider theme={theme}>
      <GlobalStyles />
      {children}
    </ThemeProvider>
  );
};

export default ThemeProviderWrapper;
