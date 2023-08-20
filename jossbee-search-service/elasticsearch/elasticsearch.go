package elasticsearch

import (
	elasticsearch "github.com/elastic/go-elasticsearch/v8"
	"net/http"
	"sync"
)

var poolSize = 10

type Client struct {
	esClient  *elasticsearch.Client
	connPool  chan *elasticsearch.Client
	connMutex sync.Mutex
}

func NewClient() (*Client, error) {
	esClient, err := elasticsearch.NewDefaultClient()
	if err != nil {
		return nil, err
	}

	connPool := make(chan *elasticsearch.Client, poolSize)
	for i := 0; i < poolSize; i++ {
		connPool <- esClient
	}

	return &Client{
		esClient:  esClient,
		connPool:  connPool,
		connMutex: sync.Mutex{},
	}, nil
}

func (c *Client) Perform(request *http.Request) (*http.Response, error) {
	conn := c.getConnFromPool()
	defer c.releaseConnToPool(conn)
	return conn.Perform(request)
}

func (c *Client) getConnFromPool() *elasticsearch.Client {
	c.connMutex.Lock()
	defer c.connMutex.Unlock()
	return <-c.connPool
}

func (c *Client) releaseConnToPool(conn *elasticsearch.Client) {
	c.connPool <- conn
}
