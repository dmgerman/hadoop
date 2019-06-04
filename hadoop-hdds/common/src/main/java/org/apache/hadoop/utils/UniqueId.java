begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.utils
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|utils
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|HddsUtils
import|;
end_import

begin_comment
comment|/**  * This class uses system current time milliseconds to generate unique id.  */
end_comment

begin_class
DECL|class|UniqueId
specifier|public
specifier|final
class|class
name|UniqueId
block|{
comment|/*      * When we represent time in milliseconds using 'long' data type,      * the LSB bits are used. Currently we are only using 44 bits (LSB),      * 20 bits (MSB) are not used.      * We will exhaust this 44 bits only when we are in year 2525,      * until then we can safely use this 20 bits (MSB) for offset to generate      * unique id within millisecond.      *      * Year        : Mon Dec 31 18:49:04 IST 2525      * TimeInMillis: 17545641544247      * Binary Representation:      *   MSB (20 bits): 0000 0000 0000 0000 0000      *   LSB (44 bits): 1111 1111 0101 0010 1001 1011 1011 0100 1010 0011 0111      *      * We have 20 bits to run counter, we should exclude the first bit (MSB)      * as we don't want to deal with negative values.      * To be on safer side we will use 'short' data type which is of length      * 16 bits and will give us 65,536 values for offset.      *      */
DECL|field|offset
specifier|private
specifier|static
specifier|volatile
name|short
name|offset
init|=
literal|0
decl_stmt|;
comment|/**    * Private constructor so that no one can instantiate this class.    */
DECL|method|UniqueId ()
specifier|private
name|UniqueId
parameter_list|()
block|{}
comment|/**    * Calculate and returns next unique id based on System#currentTimeMillis.    *    * @return unique long value    */
DECL|method|next ()
specifier|public
specifier|static
specifier|synchronized
name|long
name|next
parameter_list|()
block|{
name|long
name|utcTime
init|=
name|HddsUtils
operator|.
name|getUtcTime
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|utcTime
operator|&
literal|0xFFFF000000000000L
operator|)
operator|==
literal|0
condition|)
block|{
return|return
name|utcTime
operator|<<
name|Short
operator|.
name|SIZE
operator||
operator|(
name|offset
operator|++
operator|&
literal|0x0000FFFF
operator|)
return|;
block|}
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Got invalid UTC time,"
operator|+
literal|" cannot generate unique Id. UTC Time: "
operator|+
name|utcTime
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

