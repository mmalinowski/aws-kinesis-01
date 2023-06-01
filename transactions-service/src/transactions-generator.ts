import { randomBytes } from 'crypto';
import { Logger } from 'winston';

export class TransactionsGenerator {
  customerIds: string[] = [];
  cardIds: string[] = [];
  atmIds: string[] = [];

  constructor(
    private readonly transactionLogger: Logger,
    private readonly lockedCardsLogger: Logger,
  ) {
    for (let i = 0; i < 10000; i++) {
      this.customerIds.push(randomBytes(8).toString('hex'));
      this.cardIds.push(randomBytes(12).toString('hex'));
    }
    for (let i = 0; i < 100; i++) {
      this.atmIds.push(randomBytes(2).toString('hex'));
    }
  }

  async randomize() {
    for (let i = 0; i < 100000; i++) {
      this.generateTransaction();
      if (i % 20000 == 0) {
        this.generateLockedCard();
      }
      await this.delay(this.getRandomInt(5));
    }
  }

  generateLockedCard(): void {
    const customerIndex = this.getRandomInt(this.customerIds.length);
    const customerId = this.customerIds[customerIndex];
    const cardId = this.cardIds[customerIndex];
    this.lockedCardsLogger.info(`${customerId};${cardId}`);
  }

  generateTransaction(): void {
    const transactionId = randomBytes(10).toString('hex');
    const customerIndex = this.getRandomInt(this.customerIds.length);
    const customerId = this.customerIds[customerIndex];
    const cardId = this.cardIds[customerIndex];
    const atmId = this.atmIds[this.getRandomInt(this.atmIds.length)];
    const amount = this.getRandomInt(1000);
    this.transactionLogger.info(
      `${transactionId};${customerId};${cardId};${atmId};${amount}`,
    );
  }

  async delay(time: number) {
    return new Promise((resolve) => setTimeout(resolve, time));
  }

  getRandomInt = (max: number) => Math.floor(Math.random() * max);
}
