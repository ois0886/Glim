"use client";

import { createGlobalStyle } from 'styled-components';

const GlobalStyles = createGlobalStyle`

    :root {
        --bg-color: #000000;
        --primary-text-color: #FFFFFF;
        --secondary-text-color: #A1A1A6;
        --accent-color: #FFFFFF;
        --border-color: rgba(255, 255, 255, 0.15);
    }

    * {
        box-sizing: border-box;
        margin: 0;
        padding: 0;
    }

    html, body, #root {
        width: 100%;
        height: 100%;
        overflow: hidden; /* Prevent scrollbars */
    }

    body {
        background-color: var(--bg-color);
        color: var(--primary-text-color);
        font-family: 'Pretendard', sans-serif;
        -webkit-font-smoothing: antialiased;
    }
    
    a {
        color: inherit;
        text-decoration: none;
    }
`;

export default GlobalStyles;