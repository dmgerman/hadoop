begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * GangliaContext.java  *  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.metrics.ganglia
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics
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
name|metrics
operator|.
name|ContextFactory
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
name|metrics
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
name|metrics
operator|.
name|spi
operator|.
name|AbstractMetricsContext
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
name|metrics
operator|.
name|spi
operator|.
name|OutputRecord
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
name|metrics
operator|.
name|spi
operator|.
name|Util
import|;
end_import

begin_comment
comment|/**  * Context for sending metrics to Ganglia.  *   */
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
DECL|class|GangliaContext
specifier|public
class|class
name|GangliaContext
extends|extends
name|AbstractMetricsContext
block|{
DECL|field|PERIOD_PROPERTY
specifier|private
specifier|static
specifier|final
name|String
name|PERIOD_PROPERTY
init|=
literal|"period"
decl_stmt|;
DECL|field|SERVERS_PROPERTY
specifier|private
specifier|static
specifier|final
name|String
name|SERVERS_PROPERTY
init|=
literal|"servers"
decl_stmt|;
DECL|field|UNITS_PROPERTY
specifier|private
specifier|static
specifier|final
name|String
name|UNITS_PROPERTY
init|=
literal|"units"
decl_stmt|;
DECL|field|SLOPE_PROPERTY
specifier|private
specifier|static
specifier|final
name|String
name|SLOPE_PROPERTY
init|=
literal|"slope"
decl_stmt|;
DECL|field|TMAX_PROPERTY
specifier|private
specifier|static
specifier|final
name|String
name|TMAX_PROPERTY
init|=
literal|"tmax"
decl_stmt|;
DECL|field|DMAX_PROPERTY
specifier|private
specifier|static
specifier|final
name|String
name|DMAX_PROPERTY
init|=
literal|"dmax"
decl_stmt|;
DECL|field|DEFAULT_UNITS
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_UNITS
init|=
literal|""
decl_stmt|;
DECL|field|DEFAULT_SLOPE
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_SLOPE
init|=
literal|"both"
decl_stmt|;
DECL|field|DEFAULT_TMAX
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_TMAX
init|=
literal|60
decl_stmt|;
DECL|field|DEFAULT_DMAX
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_DMAX
init|=
literal|0
decl_stmt|;
DECL|field|DEFAULT_PORT
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_PORT
init|=
literal|8649
decl_stmt|;
DECL|field|BUFFER_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|BUFFER_SIZE
init|=
literal|1500
decl_stmt|;
comment|// as per libgmond.c
DECL|field|LOG
specifier|private
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
DECL|field|typeTable
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|Class
argument_list|,
name|String
argument_list|>
name|typeTable
init|=
operator|new
name|HashMap
argument_list|<
name|Class
argument_list|,
name|String
argument_list|>
argument_list|(
literal|5
argument_list|)
decl_stmt|;
static|static
block|{
name|typeTable
operator|.
name|put
argument_list|(
name|String
operator|.
name|class
argument_list|,
literal|"string"
argument_list|)
expr_stmt|;
name|typeTable
operator|.
name|put
argument_list|(
name|Byte
operator|.
name|class
argument_list|,
literal|"int8"
argument_list|)
expr_stmt|;
name|typeTable
operator|.
name|put
argument_list|(
name|Short
operator|.
name|class
argument_list|,
literal|"int16"
argument_list|)
expr_stmt|;
name|typeTable
operator|.
name|put
argument_list|(
name|Integer
operator|.
name|class
argument_list|,
literal|"int32"
argument_list|)
expr_stmt|;
name|typeTable
operator|.
name|put
argument_list|(
name|Long
operator|.
name|class
argument_list|,
literal|"float"
argument_list|)
expr_stmt|;
name|typeTable
operator|.
name|put
argument_list|(
name|Float
operator|.
name|class
argument_list|,
literal|"float"
argument_list|)
expr_stmt|;
block|}
DECL|field|buffer
specifier|protected
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
specifier|protected
name|int
name|offset
decl_stmt|;
DECL|field|metricsServers
specifier|protected
name|List
argument_list|<
name|?
extends|extends
name|SocketAddress
argument_list|>
name|metricsServers
decl_stmt|;
DECL|field|unitsTable
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|unitsTable
decl_stmt|;
DECL|field|slopeTable
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|slopeTable
decl_stmt|;
DECL|field|tmaxTable
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|tmaxTable
decl_stmt|;
DECL|field|dmaxTable
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|dmaxTable
decl_stmt|;
DECL|field|datagramSocket
specifier|protected
name|DatagramSocket
name|datagramSocket
decl_stmt|;
comment|/** Creates a new instance of GangliaContext */
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|method|GangliaContext ()
specifier|public
name|GangliaContext
parameter_list|()
block|{   }
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|method|init (String contextName, ContextFactory factory)
specifier|public
name|void
name|init
parameter_list|(
name|String
name|contextName
parameter_list|,
name|ContextFactory
name|factory
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|contextName
argument_list|,
name|factory
argument_list|)
expr_stmt|;
name|parseAndSetPeriod
argument_list|(
name|PERIOD_PROPERTY
argument_list|)
expr_stmt|;
name|metricsServers
operator|=
name|Util
operator|.
name|parse
argument_list|(
name|getAttribute
argument_list|(
name|SERVERS_PROPERTY
argument_list|)
argument_list|,
name|DEFAULT_PORT
argument_list|)
expr_stmt|;
name|unitsTable
operator|=
name|getAttributeTable
argument_list|(
name|UNITS_PROPERTY
argument_list|)
expr_stmt|;
name|slopeTable
operator|=
name|getAttributeTable
argument_list|(
name|SLOPE_PROPERTY
argument_list|)
expr_stmt|;
name|tmaxTable
operator|=
name|getAttributeTable
argument_list|(
name|TMAX_PROPERTY
argument_list|)
expr_stmt|;
name|dmaxTable
operator|=
name|getAttributeTable
argument_list|(
name|DMAX_PROPERTY
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
name|se
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|method|emitRecord (String contextName, String recordName, OutputRecord outRec)
specifier|public
name|void
name|emitRecord
parameter_list|(
name|String
name|contextName
parameter_list|,
name|String
name|recordName
parameter_list|,
name|OutputRecord
name|outRec
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Setup so that the records have the proper leader names so they are
comment|// unambiguous at the ganglia level, and this prevents a lot of rework
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|contextName
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'.'
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|recordName
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'.'
argument_list|)
expr_stmt|;
name|int
name|sbBaseLen
init|=
name|sb
operator|.
name|length
argument_list|()
decl_stmt|;
comment|// emit each metric in turn
for|for
control|(
name|String
name|metricName
range|:
name|outRec
operator|.
name|getMetricNames
argument_list|()
control|)
block|{
name|Object
name|metric
init|=
name|outRec
operator|.
name|getMetric
argument_list|(
name|metricName
argument_list|)
decl_stmt|;
name|String
name|type
init|=
name|typeTable
operator|.
name|get
argument_list|(
name|metric
operator|.
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|metricName
argument_list|)
expr_stmt|;
name|emitMetric
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|,
name|type
argument_list|,
name|metric
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|setLength
argument_list|(
name|sbBaseLen
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unknown metrics type: "
operator|+
name|metric
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|emitMetric (String name, String type, String value)
specifier|protected
name|void
name|emitMetric
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|type
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|units
init|=
name|getUnits
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|int
name|slope
init|=
name|getSlope
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|int
name|tmax
init|=
name|getTmax
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|int
name|dmax
init|=
name|getDmax
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|offset
operator|=
literal|0
expr_stmt|;
name|xdr_int
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// metric_user_defined
name|xdr_string
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|xdr_string
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|xdr_string
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|xdr_string
argument_list|(
name|units
argument_list|)
expr_stmt|;
name|xdr_int
argument_list|(
name|slope
argument_list|)
expr_stmt|;
name|xdr_int
argument_list|(
name|tmax
argument_list|)
expr_stmt|;
name|xdr_int
argument_list|(
name|dmax
argument_list|)
expr_stmt|;
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
DECL|method|getUnits (String metricName)
specifier|protected
name|String
name|getUnits
parameter_list|(
name|String
name|metricName
parameter_list|)
block|{
name|String
name|result
init|=
name|unitsTable
operator|.
name|get
argument_list|(
name|metricName
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
name|result
operator|=
name|DEFAULT_UNITS
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|method|getSlope (String metricName)
specifier|protected
name|int
name|getSlope
parameter_list|(
name|String
name|metricName
parameter_list|)
block|{
name|String
name|slopeString
init|=
name|slopeTable
operator|.
name|get
argument_list|(
name|metricName
argument_list|)
decl_stmt|;
if|if
condition|(
name|slopeString
operator|==
literal|null
condition|)
block|{
name|slopeString
operator|=
name|DEFAULT_SLOPE
expr_stmt|;
block|}
return|return
operator|(
literal|"zero"
operator|.
name|equals
argument_list|(
name|slopeString
argument_list|)
condition|?
literal|0
else|:
literal|3
operator|)
return|;
comment|// see gmetric.c
block|}
DECL|method|getTmax (String metricName)
specifier|protected
name|int
name|getTmax
parameter_list|(
name|String
name|metricName
parameter_list|)
block|{
if|if
condition|(
name|tmaxTable
operator|==
literal|null
condition|)
block|{
return|return
name|DEFAULT_TMAX
return|;
block|}
name|String
name|tmaxString
init|=
name|tmaxTable
operator|.
name|get
argument_list|(
name|metricName
argument_list|)
decl_stmt|;
if|if
condition|(
name|tmaxString
operator|==
literal|null
condition|)
block|{
return|return
name|DEFAULT_TMAX
return|;
block|}
else|else
block|{
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|tmaxString
argument_list|)
return|;
block|}
block|}
DECL|method|getDmax (String metricName)
specifier|protected
name|int
name|getDmax
parameter_list|(
name|String
name|metricName
parameter_list|)
block|{
name|String
name|dmaxString
init|=
name|dmaxTable
operator|.
name|get
argument_list|(
name|metricName
argument_list|)
decl_stmt|;
if|if
condition|(
name|dmaxString
operator|==
literal|null
condition|)
block|{
return|return
name|DEFAULT_DMAX
return|;
block|}
else|else
block|{
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|dmaxString
argument_list|)
return|;
block|}
block|}
comment|/**    * Puts a string into the buffer by first writing the size of the string    * as an int, followed by the bytes of the string, padded if necessary to    * a multiple of 4.    */
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
comment|/**    * Pads the buffer with zero bytes up to the nearest multiple of 4.    */
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
block|}
end_class

end_unit

