begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.metrics2.sink
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics2
operator|.
name|sink
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|configuration2
operator|.
name|SubsetConfiguration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceStability
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics2
operator|.
name|AbstractMetric
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics2
operator|.
name|MetricsException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics2
operator|.
name|MetricsRecord
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics2
operator|.
name|MetricsSink
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics2
operator|.
name|MetricsTag
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStreamWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|Socket
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import

begin_comment
comment|/**  * A metrics sink that writes to a Graphite server  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|GraphiteSink
specifier|public
class|class
name|GraphiteSink
implements|implements
name|MetricsSink
implements|,
name|Closeable
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|GraphiteSink
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|SERVER_HOST_KEY
specifier|private
specifier|static
specifier|final
name|String
name|SERVER_HOST_KEY
init|=
literal|"server_host"
decl_stmt|;
DECL|field|SERVER_PORT_KEY
specifier|private
specifier|static
specifier|final
name|String
name|SERVER_PORT_KEY
init|=
literal|"server_port"
decl_stmt|;
DECL|field|METRICS_PREFIX
specifier|private
specifier|static
specifier|final
name|String
name|METRICS_PREFIX
init|=
literal|"metrics_prefix"
decl_stmt|;
DECL|field|metricsPrefix
specifier|private
name|String
name|metricsPrefix
init|=
literal|null
decl_stmt|;
DECL|field|graphite
specifier|private
name|Graphite
name|graphite
init|=
literal|null
decl_stmt|;
annotation|@
name|Override
DECL|method|init (SubsetConfiguration conf)
specifier|public
name|void
name|init
parameter_list|(
name|SubsetConfiguration
name|conf
parameter_list|)
block|{
comment|// Get Graphite host configurations.
specifier|final
name|String
name|serverHost
init|=
name|conf
operator|.
name|getString
argument_list|(
name|SERVER_HOST_KEY
argument_list|)
decl_stmt|;
specifier|final
name|int
name|serverPort
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|conf
operator|.
name|getString
argument_list|(
name|SERVER_PORT_KEY
argument_list|)
argument_list|)
decl_stmt|;
comment|// Get Graphite metrics graph prefix.
name|metricsPrefix
operator|=
name|conf
operator|.
name|getString
argument_list|(
name|METRICS_PREFIX
argument_list|)
expr_stmt|;
if|if
condition|(
name|metricsPrefix
operator|==
literal|null
condition|)
name|metricsPrefix
operator|=
literal|""
expr_stmt|;
name|graphite
operator|=
operator|new
name|Graphite
argument_list|(
name|serverHost
argument_list|,
name|serverPort
argument_list|)
expr_stmt|;
name|graphite
operator|.
name|connect
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|putMetrics (MetricsRecord record)
specifier|public
name|void
name|putMetrics
parameter_list|(
name|MetricsRecord
name|record
parameter_list|)
block|{
name|StringBuilder
name|lines
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|StringBuilder
name|metricsPathPrefix
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
comment|// Configure the hierarchical place to display the graph.
name|metricsPathPrefix
operator|.
name|append
argument_list|(
name|metricsPrefix
argument_list|)
operator|.
name|append
argument_list|(
literal|"."
argument_list|)
operator|.
name|append
argument_list|(
name|record
operator|.
name|context
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"."
argument_list|)
operator|.
name|append
argument_list|(
name|record
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|MetricsTag
name|tag
range|:
name|record
operator|.
name|tags
argument_list|()
control|)
block|{
if|if
condition|(
name|tag
operator|.
name|value
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|metricsPathPrefix
operator|.
name|append
argument_list|(
literal|"."
argument_list|)
expr_stmt|;
name|metricsPathPrefix
operator|.
name|append
argument_list|(
name|tag
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|metricsPathPrefix
operator|.
name|append
argument_list|(
literal|"="
argument_list|)
expr_stmt|;
name|metricsPathPrefix
operator|.
name|append
argument_list|(
name|tag
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// The record timestamp is in milliseconds while Graphite expects an epoc time in seconds.
name|long
name|timestamp
init|=
name|record
operator|.
name|timestamp
argument_list|()
operator|/
literal|1000L
decl_stmt|;
comment|// Collect datapoints.
for|for
control|(
name|AbstractMetric
name|metric
range|:
name|record
operator|.
name|metrics
argument_list|()
control|)
block|{
name|lines
operator|.
name|append
argument_list|(
name|metricsPathPrefix
operator|.
name|toString
argument_list|()
operator|+
literal|"."
operator|+
name|metric
operator|.
name|name
argument_list|()
operator|.
name|replace
argument_list|(
literal|' '
argument_list|,
literal|'.'
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
operator|.
name|append
argument_list|(
name|metric
operator|.
name|value
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
operator|.
name|append
argument_list|(
name|timestamp
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|graphite
operator|.
name|write
argument_list|(
name|lines
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error sending metrics to Graphite"
argument_list|,
name|e
argument_list|)
expr_stmt|;
try|try
block|{
name|graphite
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e1
parameter_list|)
block|{
throw|throw
operator|new
name|MetricsException
argument_list|(
literal|"Error closing connection to Graphite"
argument_list|,
name|e1
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|flush ()
specifier|public
name|void
name|flush
parameter_list|()
block|{
try|try
block|{
name|graphite
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error flushing metrics to Graphite"
argument_list|,
name|e
argument_list|)
expr_stmt|;
try|try
block|{
name|graphite
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e1
parameter_list|)
block|{
throw|throw
operator|new
name|MetricsException
argument_list|(
literal|"Error closing connection to Graphite"
argument_list|,
name|e1
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|graphite
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|class|Graphite
specifier|public
specifier|static
class|class
name|Graphite
block|{
DECL|field|MAX_CONNECTION_FAILURES
specifier|private
specifier|final
specifier|static
name|int
name|MAX_CONNECTION_FAILURES
init|=
literal|5
decl_stmt|;
DECL|field|serverHost
specifier|private
name|String
name|serverHost
decl_stmt|;
DECL|field|serverPort
specifier|private
name|int
name|serverPort
decl_stmt|;
DECL|field|writer
specifier|private
name|Writer
name|writer
init|=
literal|null
decl_stmt|;
DECL|field|socket
specifier|private
name|Socket
name|socket
init|=
literal|null
decl_stmt|;
DECL|field|connectionFailures
specifier|private
name|int
name|connectionFailures
init|=
literal|0
decl_stmt|;
DECL|method|Graphite (String serverHost, int serverPort)
specifier|public
name|Graphite
parameter_list|(
name|String
name|serverHost
parameter_list|,
name|int
name|serverPort
parameter_list|)
block|{
name|this
operator|.
name|serverHost
operator|=
name|serverHost
expr_stmt|;
name|this
operator|.
name|serverPort
operator|=
name|serverPort
expr_stmt|;
block|}
DECL|method|connect ()
specifier|public
name|void
name|connect
parameter_list|()
block|{
if|if
condition|(
name|isConnected
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|MetricsException
argument_list|(
literal|"Already connected to Graphite"
argument_list|)
throw|;
block|}
if|if
condition|(
name|tooManyConnectionFailures
argument_list|()
condition|)
block|{
comment|// return silently (there was ERROR in logs when we reached limit for the first time)
return|return;
block|}
try|try
block|{
comment|// Open a connection to Graphite server.
name|socket
operator|=
operator|new
name|Socket
argument_list|(
name|serverHost
argument_list|,
name|serverPort
argument_list|)
expr_stmt|;
name|writer
operator|=
operator|new
name|OutputStreamWriter
argument_list|(
name|socket
operator|.
name|getOutputStream
argument_list|()
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|connectionFailures
operator|++
expr_stmt|;
if|if
condition|(
name|tooManyConnectionFailures
argument_list|()
condition|)
block|{
comment|// first time when connection limit reached, report to logs
name|LOG
operator|.
name|error
argument_list|(
literal|"Too many connection failures, would not try to connect again."
argument_list|)
expr_stmt|;
block|}
throw|throw
operator|new
name|MetricsException
argument_list|(
literal|"Error creating connection, "
operator|+
name|serverHost
operator|+
literal|":"
operator|+
name|serverPort
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|write (String msg)
specifier|public
name|void
name|write
parameter_list|(
name|String
name|msg
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|isConnected
argument_list|()
condition|)
block|{
name|connect
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|isConnected
argument_list|()
condition|)
block|{
name|writer
operator|.
name|write
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|flush ()
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|isConnected
argument_list|()
condition|)
block|{
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|isConnected ()
specifier|public
name|boolean
name|isConnected
parameter_list|()
block|{
return|return
name|socket
operator|!=
literal|null
operator|&&
name|socket
operator|.
name|isConnected
argument_list|()
operator|&&
operator|!
name|socket
operator|.
name|isClosed
argument_list|()
return|;
block|}
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
if|if
condition|(
name|writer
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
if|if
condition|(
name|socket
operator|!=
literal|null
condition|)
block|{
name|socket
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|socket
operator|=
literal|null
expr_stmt|;
name|writer
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|tooManyConnectionFailures ()
specifier|private
name|boolean
name|tooManyConnectionFailures
parameter_list|()
block|{
return|return
name|connectionFailures
operator|>
name|MAX_CONNECTION_FAILURES
return|;
block|}
block|}
block|}
end_class

end_unit

