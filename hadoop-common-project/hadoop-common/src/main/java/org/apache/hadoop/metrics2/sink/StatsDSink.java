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
name|net
operator|.
name|DatagramPacket
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|DatagramSocket
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
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
name|MetricType
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
name|apache
operator|.
name|hadoop
operator|.
name|metrics2
operator|.
name|impl
operator|.
name|MsInfo
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
name|net
operator|.
name|NetUtils
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

begin_comment
comment|/**  * A metrics sink that writes metrics to a StatsD daemon.  * This sink will produce metrics of the form  * '[hostname].servicename.context.name.metricname:value|type'  * where hostname is optional. This is useful when sending to  * a daemon that is running on the localhost and will add the  * hostname to the metric (such as the  *<a href="https://collectd.org/">CollectD</a> StatsD plugin).  *<br>  * To configure this plugin, you will need to add the following  * entries to your hadoop-metrics2.properties file:  *<br>  *<pre>  * *.sink.statsd.class=org.apache.hadoop.metrics2.sink.StatsDSink  * [prefix].sink.statsd.server.host=  * [prefix].sink.statsd.server.port=  * [prefix].sink.statsd.skip.hostname=true|false (optional)  * [prefix].sink.statsd.service.name=NameNode (name you want for service)  *</pre>  */
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
DECL|class|StatsDSink
specifier|public
class|class
name|StatsDSink
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
name|StatsDSink
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|PERIOD
specifier|private
specifier|static
specifier|final
name|String
name|PERIOD
init|=
literal|"."
decl_stmt|;
DECL|field|SERVER_HOST_KEY
specifier|private
specifier|static
specifier|final
name|String
name|SERVER_HOST_KEY
init|=
literal|"server.host"
decl_stmt|;
DECL|field|SERVER_PORT_KEY
specifier|private
specifier|static
specifier|final
name|String
name|SERVER_PORT_KEY
init|=
literal|"server.port"
decl_stmt|;
DECL|field|HOST_NAME_KEY
specifier|private
specifier|static
specifier|final
name|String
name|HOST_NAME_KEY
init|=
literal|"host.name"
decl_stmt|;
DECL|field|SERVICE_NAME_KEY
specifier|private
specifier|static
specifier|final
name|String
name|SERVICE_NAME_KEY
init|=
literal|"service.name"
decl_stmt|;
DECL|field|SKIP_HOSTNAME_KEY
specifier|private
specifier|static
specifier|final
name|String
name|SKIP_HOSTNAME_KEY
init|=
literal|"skip.hostname"
decl_stmt|;
DECL|field|skipHostname
specifier|private
name|boolean
name|skipHostname
init|=
literal|false
decl_stmt|;
DECL|field|hostName
specifier|private
name|String
name|hostName
init|=
literal|null
decl_stmt|;
DECL|field|serviceName
specifier|private
name|String
name|serviceName
init|=
literal|null
decl_stmt|;
DECL|field|statsd
specifier|private
name|StatsD
name|statsd
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
comment|// Get StatsD host configurations.
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
name|skipHostname
operator|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|SKIP_HOSTNAME_KEY
argument_list|,
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|skipHostname
condition|)
block|{
name|hostName
operator|=
name|conf
operator|.
name|getString
argument_list|(
name|HOST_NAME_KEY
argument_list|,
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|==
name|hostName
condition|)
block|{
name|hostName
operator|=
name|NetUtils
operator|.
name|getHostname
argument_list|()
expr_stmt|;
block|}
block|}
name|serviceName
operator|=
name|conf
operator|.
name|getString
argument_list|(
name|SERVICE_NAME_KEY
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|statsd
operator|=
operator|new
name|StatsD
argument_list|(
name|serverHost
argument_list|,
name|serverPort
argument_list|)
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
name|String
name|hn
init|=
name|hostName
decl_stmt|;
name|String
name|ctx
init|=
name|record
operator|.
name|context
argument_list|()
decl_stmt|;
name|String
name|sn
init|=
name|serviceName
decl_stmt|;
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
name|info
argument_list|()
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
name|MsInfo
operator|.
name|Hostname
operator|.
name|name
argument_list|()
argument_list|)
operator|&&
name|tag
operator|.
name|value
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|hn
operator|=
name|tag
operator|.
name|value
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|tag
operator|.
name|info
argument_list|()
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
name|MsInfo
operator|.
name|Context
operator|.
name|name
argument_list|()
argument_list|)
operator|&&
name|tag
operator|.
name|value
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|ctx
operator|=
name|tag
operator|.
name|value
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|tag
operator|.
name|info
argument_list|()
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
name|MsInfo
operator|.
name|ProcessName
operator|.
name|name
argument_list|()
argument_list|)
operator|&&
name|tag
operator|.
name|value
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|sn
operator|=
name|tag
operator|.
name|value
argument_list|()
expr_stmt|;
block|}
block|}
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|skipHostname
operator|&&
name|hn
operator|!=
literal|null
condition|)
block|{
name|int
name|idx
init|=
name|hn
operator|.
name|indexOf
argument_list|(
literal|"."
argument_list|)
decl_stmt|;
if|if
condition|(
name|idx
operator|==
operator|-
literal|1
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|hn
argument_list|)
operator|.
name|append
argument_list|(
name|PERIOD
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buf
operator|.
name|append
argument_list|(
name|hn
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|idx
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
name|PERIOD
argument_list|)
expr_stmt|;
block|}
block|}
name|buf
operator|.
name|append
argument_list|(
name|sn
argument_list|)
operator|.
name|append
argument_list|(
name|PERIOD
argument_list|)
operator|.
name|append
argument_list|(
name|ctx
argument_list|)
operator|.
name|append
argument_list|(
name|PERIOD
argument_list|)
operator|.
name|append
argument_list|(
name|record
operator|.
name|name
argument_list|()
operator|.
name|replaceAll
argument_list|(
literal|"\\."
argument_list|,
literal|"-"
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
name|PERIOD
argument_list|)
expr_stmt|;
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
name|String
name|type
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|metric
operator|.
name|type
argument_list|()
operator|.
name|equals
argument_list|(
name|MetricType
operator|.
name|COUNTER
argument_list|)
condition|)
block|{
name|type
operator|=
literal|"c"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|metric
operator|.
name|type
argument_list|()
operator|.
name|equals
argument_list|(
name|MetricType
operator|.
name|GAUGE
argument_list|)
condition|)
block|{
name|type
operator|=
literal|"g"
expr_stmt|;
block|}
name|StringBuilder
name|line
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|line
operator|.
name|append
argument_list|(
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|metric
operator|.
name|name
argument_list|()
operator|.
name|replace
argument_list|(
literal|' '
argument_list|,
literal|'_'
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|":"
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
literal|"|"
argument_list|)
operator|.
name|append
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|writeMetric
argument_list|(
name|line
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|writeMetric (String line)
specifier|public
name|void
name|writeMetric
parameter_list|(
name|String
name|line
parameter_list|)
block|{
try|try
block|{
name|statsd
operator|.
name|write
argument_list|(
name|line
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error sending metrics to StatsD"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|MetricsException
argument_list|(
literal|"Error writing metric to StatsD"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|flush ()
specifier|public
name|void
name|flush
parameter_list|()
block|{   }
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
name|statsd
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Class that sends UDP packets to StatsD daemon.    *    */
DECL|class|StatsD
specifier|public
specifier|static
class|class
name|StatsD
block|{
DECL|field|socket
specifier|private
name|DatagramSocket
name|socket
init|=
literal|null
decl_stmt|;
DECL|field|packet
specifier|private
name|DatagramPacket
name|packet
init|=
literal|null
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
DECL|method|StatsD (String serverHost, int serverPort)
specifier|public
name|StatsD
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
DECL|method|createSocket ()
specifier|public
name|void
name|createSocket
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|InetSocketAddress
name|address
init|=
operator|new
name|InetSocketAddress
argument_list|(
name|this
operator|.
name|serverHost
argument_list|,
name|this
operator|.
name|serverPort
argument_list|)
decl_stmt|;
name|socket
operator|=
operator|new
name|DatagramSocket
argument_list|()
expr_stmt|;
name|packet
operator|=
operator|new
name|DatagramPacket
argument_list|(
literal|""
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|address
operator|.
name|getAddress
argument_list|()
argument_list|,
name|this
operator|.
name|serverPort
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
name|NetUtils
operator|.
name|wrapException
argument_list|(
name|this
operator|.
name|serverHost
argument_list|,
name|this
operator|.
name|serverPort
argument_list|,
literal|"localhost"
argument_list|,
literal|0
argument_list|,
name|ioe
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
literal|null
operator|==
name|socket
condition|)
block|{
name|createSocket
argument_list|()
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Sending metric: {}"
argument_list|,
name|msg
argument_list|)
expr_stmt|;
name|packet
operator|.
name|setData
argument_list|(
name|msg
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
name|socket
operator|.
name|send
argument_list|(
name|packet
argument_list|)
expr_stmt|;
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
block|}
block|}
block|}
block|}
end_class

end_unit

