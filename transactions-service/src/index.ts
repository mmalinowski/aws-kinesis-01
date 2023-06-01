import { TransactionsGenerator } from './transactions-generator';
import { lockedCardLogger, transactionLogger } from './loggers';

const transactionsGenerator = new TransactionsGenerator(
  transactionLogger,
  lockedCardLogger,
);
transactionsGenerator.randomize().then(() => {
  console.log('done');
  while (true);
});
