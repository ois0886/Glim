import type { Metadata } from "next";
import StyledComponentsRegistry from '../lib/registry';
import ThemeProviderWrapper from '../components/ThemeProviderWrapper';

export const metadata: Metadata = {
  title: "GLIM",
  description: "Cosmos of Sentences",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body>
        <StyledComponentsRegistry>
          <ThemeProviderWrapper>
            {children}
          </ThemeProviderWrapper>
        </StyledComponentsRegistry>
      </body>
    </html>
  );
}
