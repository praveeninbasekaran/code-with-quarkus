import { createContext, contexts } from '@scdevkit/data';
import { ERROR_MESSAGES } from '../utils/errorConstants';

const graphQLClientContext = createContext(contexts.GRAPHQL_CLIENT);

export const serviceApiHandler = async (thisArg: any, query: string) => {
  const graphQLClient = graphQLClientContext.createConsumer(thisArg).value;

  function handleApiError(status: number): string {
    return ERROR_MESSAGES[status] || ERROR_MESSAGES.UNKNOWN_ERROR;
  }

  let response;
  try {
    response = await graphQLClient.query(query);

    if (!response.ok) {
      const errorMessage = handleApiError(response.status);
      throw new Error(errorMessage);
    }

    const json = await response.json();
    if (json.data?._55313_113_exp_api) {
      return json.data._55313_113_exp_api;
    }
    return json.data;

  } catch (error) {
    console.error('ERROR IN API SERVICE: ', error);
    throw error;
  }
};

// ✅ NEW - Dedicated fetch method for Excel export
export const fetchExportData = async (thisArg: any, query: string) => {
  const graphQLClient = graphQLClientContext.createConsumer(thisArg).value;

  function handleApiError(status: number): string {
    return ERROR_MESSAGES[status] || ERROR_MESSAGES.UNKNOWN_ERROR;
  }

  try {
    const response = await graphQLClient.query(query);

    if (!response.ok) {
      const errorMessage = handleApiError(response.status);
      throw new Error(errorMessage);
    }

    const json = await response.json();
    if (json.data?._55313_113_exp_api?.post_export) {
      return json.data._55313_113_exp_api.post_export;
    }

    throw new Error('Export data not found in response');

  } catch (error) {
    console.error('ERROR IN EXPORT API SERVICE: ', error);
    throw error;
  }
};