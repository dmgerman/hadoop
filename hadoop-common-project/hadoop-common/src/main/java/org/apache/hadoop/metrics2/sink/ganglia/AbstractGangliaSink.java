begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.metrics2.sink.ganglia
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
operator|.
name|ganglia
package|;
end_package

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
name|SocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|SocketException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|UnknownHostException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|configuration
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
name|commons
operator|.
name|logging
operator|.
name|Log
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
name|logging
operator|.
name|LogFactory
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
name|util
operator|.
name|Servers
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
name|DNS
import|;
end_import

begin_comment
comment|/**  * This the base class for Ganglia sink classes using metrics2. Lot of the code  * has been derived from org.apache.hadoop.metrics.ganglia.GangliaContext.  * As per the documentation, sink implementations doesn't have to worry about  * thread safety. Hence the code wasn't written for thread safety and should  * be modified in case the above assumption changes in the future.  */
end_comment

begin_class
DECL|class|AbstractGangliaSink
specifier|public
specifier|abstract
class|class
name|AbstractGangliaSink
implements|implements
name|MetricsSink
block|{
DECL|field|LOG
specifier|public
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
comment|/*    * Output of "gmetric --help" showing allowable values    * -t, --type=STRING    *     Either string|int8|uint8|int16|uint16|int32|uint32|float|double    * -u, --units=STRING Unit of measure for the value e.g. Kilobytes, Celcius    *     (default='')    * -s, --slope=STRING Either zero|positive|negative|both    *     (default='both')    * -x, --tmax=INT The maximum time in seconds between gmetric calls    *     (default='60')    */
DECL|field|DEFAULT_UNITS
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_UNITS
init|=
literal|""
decl_stmt|;
DECL|field|DEFAULT_TMAX
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_TMAX
init|=
literal|60
decl_stmt|;
DECL|field|DEFAULT_DMAX
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_DMAX
init|=
literal|0
decl_stmt|;
DECL|field|DEFAULT_SLOPE
specifier|public
specifier|static
specifier|final
name|GangliaSlope
name|DEFAULT_SLOPE
init|=
name|GangliaSlope
operator|.
name|both
decl_stmt|;
DECL|field|DEFAULT_PORT
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_PORT
init|=
literal|8649
decl_stmt|;
DECL|field|SERVERS_PROPERTY
specifier|public
specifier|static
specifier|final
name|String
name|SERVERS_PROPERTY
init|=
literal|"servers"
decl_stmt|;
DECL|field|BUFFER_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|BUFFER_SIZE
init|=
literal|1500
decl_stmt|;
comment|// as per libgmond.c
DECL|field|SUPPORT_SPARSE_METRICS_PROPERTY
specifier|public
specifier|static
specifier|final
name|String
name|SUPPORT_SPARSE_METRICS_PROPERTY
init|=
literal|"supportsparse"
decl_stmt|;
DECL|field|SUPPORT_SPARSE_METRICS_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|SUPPORT_SPARSE_METRICS_DEFAULT
init|=
literal|false
decl_stmt|;
DECL|field|EQUAL
specifier|public
specifier|static
specifier|final
name|String
name|EQUAL
init|=
literal|"="
decl_stmt|;
DECL|field|hostName
specifier|private
name|String
name|hostName
init|=
literal|"UNKNOWN.example.com"
decl_stmt|;
DECL|field|datagramSocket
specifier|private
name|DatagramSocket
name|datagramSocket
decl_stmt|;
DECL|field|metricsServers
specifier|private
name|List
argument_list|<
name|?
extends|extends
name|SocketAddress
argument_list|>
name|metricsServers
decl_stmt|;
DECL|field|buffer
specifier|private
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
name|BUFFER_SIZE
index|]
decl_stmt|;
DECL|field|offset
specifier|private
name|int
name|offset
decl_stmt|;
DECL|field|supportSparseMetrics
specifier|private
name|boolean
name|supportSparseMetrics
init|=
name|SUPPORT_SPARSE_METRICS_DEFAULT
decl_stmt|;
comment|/**    * Used for visiting Metrics    */
DECL|field|gangliaMetricVisitor
specifier|protected
specifier|final
name|GangliaMetricVisitor
name|gangliaMetricVisitor
init|=
operator|new
name|GangliaMetricVisitor
argument_list|()
decl_stmt|;
DECL|field|conf
specifier|private
name|SubsetConfiguration
name|conf
decl_stmt|;
DECL|field|gangliaConfMap
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|GangliaConf
argument_list|>
name|gangliaConfMap
decl_stmt|;
DECL|field|DEFAULT_GANGLIA_CONF
specifier|private
name|GangliaConf
name|DEFAULT_GANGLIA_CONF
init|=
operator|new
name|GangliaConf
argument_list|()
decl_stmt|;
comment|/**    * ganglia slope values which equal the ordinal    */
DECL|enum|GangliaSlope
specifier|public
enum|enum
name|GangliaSlope
block|{
DECL|enumConstant|zero
name|zero
block|,
comment|// 0
DECL|enumConstant|positive
name|positive
block|,
comment|// 1
DECL|enumConstant|negative
name|negative
block|,
comment|// 2
DECL|enumConstant|both
name|both
comment|// 3
block|}
empty_stmt|;
comment|/**    * define enum for various type of conf    */
DECL|enum|GangliaConfType
specifier|public
enum|enum
name|GangliaConfType
block|{
DECL|enumConstant|slope
DECL|enumConstant|units
DECL|enumConstant|dmax
DECL|enumConstant|tmax
name|slope
block|,
name|units
block|,
name|dmax
block|,
name|tmax
block|}
empty_stmt|;
comment|/*    * (non-Javadoc)    *    * @see    * org.apache.hadoop.metrics2.MetricsPlugin#init(org.apache.commons.configuration    * .SubsetConfiguration)    */
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
name|LOG
operator|.
name|debug
argument_list|(
literal|"Initializing the GangliaSink for Ganglia metrics."
argument_list|)
expr_stmt|;
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
comment|// Take the hostname from the DNS class.
if|if
condition|(
name|conf
operator|.
name|getString
argument_list|(
literal|"slave.host.name"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|hostName
operator|=
name|conf
operator|.
name|getString
argument_list|(
literal|"slave.host.name"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|hostName
operator|=
name|DNS
operator|.
name|getDefaultHost
argument_list|(
name|conf
operator|.
name|getString
argument_list|(
literal|"dfs.datanode.dns.interface"
argument_list|,
literal|"default"
argument_list|)
argument_list|,
name|conf
operator|.
name|getString
argument_list|(
literal|"dfs.datanode.dns.nameserver"
argument_list|,
literal|"default"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnknownHostException
name|uhe
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|uhe
argument_list|)
expr_stmt|;
name|hostName
operator|=
literal|"UNKNOWN.example.com"
expr_stmt|;
block|}
block|}
comment|// load the gannglia servers from properties
name|metricsServers
operator|=
name|Servers
operator|.
name|parse
argument_list|(
name|conf
operator|.
name|getString
argument_list|(
name|SERVERS_PROPERTY
argument_list|)
argument_list|,
name|DEFAULT_PORT
argument_list|)
expr_stmt|;
comment|// extract the Ganglia conf per metrics
name|gangliaConfMap
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|GangliaConf
argument_list|>
argument_list|()
expr_stmt|;
name|loadGangliaConf
argument_list|(
name|GangliaConfType
operator|.
name|units
argument_list|)
expr_stmt|;
name|loadGangliaConf
argument_list|(
name|GangliaConfType
operator|.
name|tmax
argument_list|)
expr_stmt|;
name|loadGangliaConf
argument_list|(
name|GangliaConfType
operator|.
name|dmax
argument_list|)
expr_stmt|;
name|loadGangliaConf
argument_list|(
name|GangliaConfType
operator|.
name|slope
argument_list|)
expr_stmt|;
try|try
block|{
name|datagramSocket
operator|=
operator|new
name|DatagramSocket
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SocketException
name|se
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|se
argument_list|)
expr_stmt|;
block|}
comment|// see if sparseMetrics is supported. Default is false
name|supportSparseMetrics
operator|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|SUPPORT_SPARSE_METRICS_PROPERTY
argument_list|,
name|SUPPORT_SPARSE_METRICS_DEFAULT
argument_list|)
expr_stmt|;
block|}
comment|/*    * (non-Javadoc)    *    * @see org.apache.hadoop.metrics2.MetricsSink#flush()    */
annotation|@
name|Override
DECL|method|flush ()
specifier|public
name|void
name|flush
parameter_list|()
block|{
comment|// nothing to do as we are not buffering data
block|}
comment|// Load the configurations for a conf type
DECL|method|loadGangliaConf (GangliaConfType gtype)
specifier|private
name|void
name|loadGangliaConf
parameter_list|(
name|GangliaConfType
name|gtype
parameter_list|)
block|{
name|String
name|propertyarr
index|[]
init|=
name|conf
operator|.
name|getStringArray
argument_list|(
name|gtype
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|propertyarr
operator|!=
literal|null
operator|&&
name|propertyarr
operator|.
name|length
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|String
name|metricNValue
range|:
name|propertyarr
control|)
block|{
name|String
name|metricNValueArr
index|[]
init|=
name|metricNValue
operator|.
name|split
argument_list|(
name|EQUAL
argument_list|)
decl_stmt|;
if|if
condition|(
name|metricNValueArr
operator|.
name|length
operator|!=
literal|2
operator|||
name|metricNValueArr
index|[
literal|0
index|]
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Invalid propertylist for "
operator|+
name|gtype
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|String
name|metricName
init|=
name|metricNValueArr
index|[
literal|0
index|]
operator|.
name|trim
argument_list|()
decl_stmt|;
name|String
name|metricValue
init|=
name|metricNValueArr
index|[
literal|1
index|]
operator|.
name|trim
argument_list|()
decl_stmt|;
name|GangliaConf
name|gconf
init|=
name|gangliaConfMap
operator|.
name|get
argument_list|(
name|metricName
argument_list|)
decl_stmt|;
if|if
condition|(
name|gconf
operator|==
literal|null
condition|)
block|{
name|gconf
operator|=
operator|new
name|GangliaConf
argument_list|()
expr_stmt|;
name|gangliaConfMap
operator|.
name|put
argument_list|(
name|metricName
argument_list|,
name|gconf
argument_list|)
expr_stmt|;
block|}
switch|switch
condition|(
name|gtype
condition|)
block|{
case|case
name|units
case|:
name|gconf
operator|.
name|setUnits
argument_list|(
name|metricValue
argument_list|)
expr_stmt|;
break|break;
case|case
name|dmax
case|:
name|gconf
operator|.
name|setDmax
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|metricValue
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|tmax
case|:
name|gconf
operator|.
name|setTmax
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|metricValue
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|slope
case|:
name|gconf
operator|.
name|setSlope
argument_list|(
name|GangliaSlope
operator|.
name|valueOf
argument_list|(
name|metricValue
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
comment|/**    * Lookup GangliaConf from cache. If not found, return default values    *    * @param metricName    * @return looked up GangliaConf    */
DECL|method|getGangliaConfForMetric (String metricName)
specifier|protected
name|GangliaConf
name|getGangliaConfForMetric
parameter_list|(
name|String
name|metricName
parameter_list|)
block|{
name|GangliaConf
name|gconf
init|=
name|gangliaConfMap
operator|.
name|get
argument_list|(
name|metricName
argument_list|)
decl_stmt|;
return|return
name|gconf
operator|!=
literal|null
condition|?
name|gconf
else|:
name|DEFAULT_GANGLIA_CONF
return|;
block|}
comment|/**    * @return the hostName    */
DECL|method|getHostName ()
specifier|protected
name|String
name|getHostName
parameter_list|()
block|{
return|return
name|hostName
return|;
block|}
comment|/**    * Puts a string into the buffer by first writing the size of the string as an    * int, followed by the bytes of the string, padded if necessary to a multiple    * of 4.    * @param s the string to be written to buffer at offset location    */
DECL|method|xdr_string (String s)
specifier|protected
name|void
name|xdr_string
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|byte
index|[]
name|bytes
init|=
name|s
operator|.
name|getBytes
argument_list|()
decl_stmt|;
name|int
name|len
init|=
name|bytes
operator|.
name|length
decl_stmt|;
name|xdr_int
argument_list|(
name|len
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|buffer
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|offset
operator|+=
name|len
expr_stmt|;
name|pad
argument_list|()
expr_stmt|;
block|}
comment|// Pads the buffer with zero bytes up to the nearest multiple of 4.
DECL|method|pad ()
specifier|private
name|void
name|pad
parameter_list|()
block|{
name|int
name|newOffset
init|=
operator|(
operator|(
name|offset
operator|+
literal|3
operator|)
operator|/
literal|4
operator|)
operator|*
literal|4
decl_stmt|;
while|while
condition|(
name|offset
operator|<
name|newOffset
condition|)
block|{
name|buffer
index|[
name|offset
operator|++
index|]
operator|=
literal|0
expr_stmt|;
block|}
block|}
comment|/**    * Puts an integer into the buffer as 4 bytes, big-endian.    */
DECL|method|xdr_int (int i)
specifier|protected
name|void
name|xdr_int
parameter_list|(
name|int
name|i
parameter_list|)
block|{
name|buffer
index|[
name|offset
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
operator|(
name|i
operator|>>
literal|24
operator|)
operator|&
literal|0xff
argument_list|)
expr_stmt|;
name|buffer
index|[
name|offset
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
operator|(
name|i
operator|>>
literal|16
operator|)
operator|&
literal|0xff
argument_list|)
expr_stmt|;
name|buffer
index|[
name|offset
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
operator|(
name|i
operator|>>
literal|8
operator|)
operator|&
literal|0xff
argument_list|)
expr_stmt|;
name|buffer
index|[
name|offset
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|i
operator|&
literal|0xff
argument_list|)
expr_stmt|;
block|}
comment|/**    * Sends Ganglia Metrics to the configured hosts    * @throws IOException    */
DECL|method|emitToGangliaHosts ()
specifier|protected
name|void
name|emitToGangliaHosts
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
for|for
control|(
name|SocketAddress
name|socketAddress
range|:
name|metricsServers
control|)
block|{
name|DatagramPacket
name|packet
init|=
operator|new
name|DatagramPacket
argument_list|(
name|buffer
argument_list|,
name|offset
argument_list|,
name|socketAddress
argument_list|)
decl_stmt|;
name|datagramSocket
operator|.
name|send
argument_list|(
name|packet
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
comment|// reset the buffer for the next metric to be built
name|offset
operator|=
literal|0
expr_stmt|;
block|}
block|}
comment|/**    * Reset the buffer for the next metric to be built    */
DECL|method|resetBuffer ()
name|void
name|resetBuffer
parameter_list|()
block|{
name|offset
operator|=
literal|0
expr_stmt|;
block|}
comment|/**    * @return whether sparse metrics are supported    */
DECL|method|isSupportSparseMetrics ()
specifier|protected
name|boolean
name|isSupportSparseMetrics
parameter_list|()
block|{
return|return
name|supportSparseMetrics
return|;
block|}
comment|/**    * Used only by unit test    * @param datagramSocket the datagramSocket to set.    */
DECL|method|setDatagramSocket (DatagramSocket datagramSocket)
name|void
name|setDatagramSocket
parameter_list|(
name|DatagramSocket
name|datagramSocket
parameter_list|)
block|{
name|this
operator|.
name|datagramSocket
operator|=
name|datagramSocket
expr_stmt|;
block|}
block|}
end_class

end_unit

