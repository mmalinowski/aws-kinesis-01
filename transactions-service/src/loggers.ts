import { createLogger, format, transports } from 'winston';

export const lockedCardLogger = createLogger({
  transports: [
    new transports.File({
      filename: 'logs/locked-cards.log',
    }),
  ],
  format: format.printf(({ message }) => {
    return `${message}`;
  }),
});

export const transactionLogger = createLogger({
  transports: [
    new transports.File({
      filename: 'logs/transactions.log',
    }),
    new transports.Console(),
  ],
  format: format.printf(({ message }) => {
    return `${new Date().getTime()};${message}`;
  }),
});
