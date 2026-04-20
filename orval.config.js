module.exports = {
  task: {
    input: {
      target: './libs/api-contract/openapi.yaml',
    },
    output: {
      mode: 'tags-split',
      target: './libs/task-client/src/lib/generated/task-client.ts',
      schemas: './libs/task-client/src/lib/generated/model',
      client: 'angular',
      mock: true,
      prettier: true,
    },
  },
};
