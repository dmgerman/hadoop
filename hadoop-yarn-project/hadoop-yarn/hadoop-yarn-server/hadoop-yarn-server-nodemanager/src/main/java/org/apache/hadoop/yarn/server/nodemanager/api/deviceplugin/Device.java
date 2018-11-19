begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.api.deviceplugin
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|nodemanager
operator|.
name|api
operator|.
name|deviceplugin
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_comment
comment|/**  * Represent one "device" resource.  * */
end_comment

begin_class
DECL|class|Device
specifier|public
specifier|final
class|class
name|Device
implements|implements
name|Serializable
implements|,
name|Comparable
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|7270474563684671656L
decl_stmt|;
comment|/**    * An plugin specified index number.    * Must set. Recommend starting from 0    * */
DECL|field|id
specifier|private
specifier|final
name|int
name|id
decl_stmt|;
comment|/**    * The device node like "/dev/devname".    * Optional    * */
DECL|field|devPath
specifier|private
specifier|final
name|String
name|devPath
decl_stmt|;
comment|/**    * The major device number.    * Optional    * */
DECL|field|majorNumber
specifier|private
specifier|final
name|int
name|majorNumber
decl_stmt|;
comment|/**    * The minor device number.    * Optional    * */
DECL|field|minorNumber
specifier|private
specifier|final
name|int
name|minorNumber
decl_stmt|;
comment|/**    * PCI Bus ID in format [[[[<domain>]:]<bus>]:][<slot>][.[<func>]].    * Optional. Can get from "lspci -D" in Linux    * */
DECL|field|busID
specifier|private
specifier|final
name|String
name|busID
decl_stmt|;
comment|/**    * Is healthy or not.    * false by default    * */
DECL|field|isHealthy
specifier|private
name|boolean
name|isHealthy
decl_stmt|;
comment|/**    * Plugin customized status info.    * Optional    * */
DECL|field|status
specifier|private
name|String
name|status
decl_stmt|;
comment|/**    * Private constructor.    * @param builder    */
DECL|method|Device (Builder builder)
specifier|private
name|Device
parameter_list|(
name|Builder
name|builder
parameter_list|)
block|{
if|if
condition|(
name|builder
operator|.
name|id
operator|==
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Please set the id for Device"
argument_list|)
throw|;
block|}
name|this
operator|.
name|id
operator|=
name|builder
operator|.
name|id
expr_stmt|;
name|this
operator|.
name|devPath
operator|=
name|builder
operator|.
name|devPath
expr_stmt|;
name|this
operator|.
name|majorNumber
operator|=
name|builder
operator|.
name|majorNumber
expr_stmt|;
name|this
operator|.
name|minorNumber
operator|=
name|builder
operator|.
name|minorNumber
expr_stmt|;
name|this
operator|.
name|busID
operator|=
name|builder
operator|.
name|busID
expr_stmt|;
name|this
operator|.
name|isHealthy
operator|=
name|builder
operator|.
name|isHealthy
expr_stmt|;
name|this
operator|.
name|status
operator|=
name|builder
operator|.
name|status
expr_stmt|;
block|}
DECL|method|getId ()
specifier|public
name|int
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
DECL|method|getDevPath ()
specifier|public
name|String
name|getDevPath
parameter_list|()
block|{
return|return
name|devPath
return|;
block|}
DECL|method|getMajorNumber ()
specifier|public
name|int
name|getMajorNumber
parameter_list|()
block|{
return|return
name|majorNumber
return|;
block|}
DECL|method|getMinorNumber ()
specifier|public
name|int
name|getMinorNumber
parameter_list|()
block|{
return|return
name|minorNumber
return|;
block|}
DECL|method|getBusID ()
specifier|public
name|String
name|getBusID
parameter_list|()
block|{
return|return
name|busID
return|;
block|}
DECL|method|isHealthy ()
specifier|public
name|boolean
name|isHealthy
parameter_list|()
block|{
return|return
name|isHealthy
return|;
block|}
DECL|method|getStatus ()
specifier|public
name|String
name|getStatus
parameter_list|()
block|{
return|return
name|status
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|Device
name|device
init|=
operator|(
name|Device
operator|)
name|o
decl_stmt|;
return|return
name|id
operator|==
name|device
operator|.
name|getId
argument_list|()
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|devPath
argument_list|,
name|device
operator|.
name|getDevPath
argument_list|()
argument_list|)
operator|&&
name|majorNumber
operator|==
name|device
operator|.
name|getMajorNumber
argument_list|()
operator|&&
name|minorNumber
operator|==
name|device
operator|.
name|getMinorNumber
argument_list|()
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|busID
argument_list|,
name|device
operator|.
name|getBusID
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|id
argument_list|,
name|devPath
argument_list|,
name|majorNumber
argument_list|,
name|minorNumber
argument_list|,
name|busID
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|compareTo (Object o)
specifier|public
name|int
name|compareTo
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|==
literal|null
operator|||
operator|(
operator|!
operator|(
name|o
operator|instanceof
name|Device
operator|)
operator|)
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
name|Device
name|other
init|=
operator|(
name|Device
operator|)
name|o
decl_stmt|;
name|int
name|result
init|=
name|Integer
operator|.
name|compare
argument_list|(
name|id
argument_list|,
name|other
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
literal|0
operator|!=
name|result
condition|)
block|{
return|return
name|result
return|;
block|}
name|result
operator|=
name|Integer
operator|.
name|compare
argument_list|(
name|majorNumber
argument_list|,
name|other
operator|.
name|getMajorNumber
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
literal|0
operator|!=
name|result
condition|)
block|{
return|return
name|result
return|;
block|}
name|result
operator|=
name|Integer
operator|.
name|compare
argument_list|(
name|minorNumber
argument_list|,
name|other
operator|.
name|getMinorNumber
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
literal|0
operator|!=
name|result
condition|)
block|{
return|return
name|result
return|;
block|}
name|result
operator|=
name|devPath
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|getDevPath
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
literal|0
operator|!=
name|result
condition|)
block|{
return|return
name|result
return|;
block|}
return|return
name|busID
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|getBusID
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"("
operator|+
name|getId
argument_list|()
operator|+
literal|", "
operator|+
name|getDevPath
argument_list|()
operator|+
literal|", "
operator|+
name|getMajorNumber
argument_list|()
operator|+
literal|":"
operator|+
name|getMinorNumber
argument_list|()
operator|+
literal|")"
return|;
block|}
comment|/**    * Builder for Device.    * */
DECL|class|Builder
specifier|public
specifier|final
specifier|static
class|class
name|Builder
block|{
comment|// default -1 representing the value is not set
DECL|field|id
specifier|private
name|int
name|id
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|devPath
specifier|private
name|String
name|devPath
init|=
literal|""
decl_stmt|;
DECL|field|majorNumber
specifier|private
name|int
name|majorNumber
decl_stmt|;
DECL|field|minorNumber
specifier|private
name|int
name|minorNumber
decl_stmt|;
DECL|field|busID
specifier|private
name|String
name|busID
init|=
literal|""
decl_stmt|;
DECL|field|isHealthy
specifier|private
name|boolean
name|isHealthy
decl_stmt|;
DECL|field|status
specifier|private
name|String
name|status
init|=
literal|""
decl_stmt|;
DECL|method|newInstance ()
specifier|public
specifier|static
name|Builder
name|newInstance
parameter_list|()
block|{
return|return
operator|new
name|Builder
argument_list|()
return|;
block|}
DECL|method|build ()
specifier|public
name|Device
name|build
parameter_list|()
block|{
return|return
operator|new
name|Device
argument_list|(
name|this
argument_list|)
return|;
block|}
DECL|method|setId (int i)
specifier|public
name|Builder
name|setId
parameter_list|(
name|int
name|i
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|i
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setDevPath (String dp)
specifier|public
name|Builder
name|setDevPath
parameter_list|(
name|String
name|dp
parameter_list|)
block|{
name|this
operator|.
name|devPath
operator|=
name|dp
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setMajorNumber (int maN)
specifier|public
name|Builder
name|setMajorNumber
parameter_list|(
name|int
name|maN
parameter_list|)
block|{
name|this
operator|.
name|majorNumber
operator|=
name|maN
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setMinorNumber (int miN)
specifier|public
name|Builder
name|setMinorNumber
parameter_list|(
name|int
name|miN
parameter_list|)
block|{
name|this
operator|.
name|minorNumber
operator|=
name|miN
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setBusID (String bI)
specifier|public
name|Builder
name|setBusID
parameter_list|(
name|String
name|bI
parameter_list|)
block|{
name|this
operator|.
name|busID
operator|=
name|bI
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setHealthy (boolean healthy)
specifier|public
name|Builder
name|setHealthy
parameter_list|(
name|boolean
name|healthy
parameter_list|)
block|{
name|isHealthy
operator|=
name|healthy
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setStatus (String s)
specifier|public
name|Builder
name|setStatus
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|this
operator|.
name|status
operator|=
name|s
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
block|}
end_class

end_unit

