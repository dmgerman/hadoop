begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io.erasurecode
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|erasurecode
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
name|classification
operator|.
name|InterfaceAudience
import|;
end_import

begin_comment
comment|/**  * Erasure coder configuration that maintains schema info and coder options.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|ErasureCoderOptions
specifier|public
specifier|final
class|class
name|ErasureCoderOptions
block|{
DECL|field|numDataUnits
specifier|private
specifier|final
name|int
name|numDataUnits
decl_stmt|;
DECL|field|numParityUnits
specifier|private
specifier|final
name|int
name|numParityUnits
decl_stmt|;
DECL|field|numAllUnits
specifier|private
specifier|final
name|int
name|numAllUnits
decl_stmt|;
DECL|field|allowChangeInputs
specifier|private
specifier|final
name|boolean
name|allowChangeInputs
decl_stmt|;
DECL|field|allowVerboseDump
specifier|private
specifier|final
name|boolean
name|allowVerboseDump
decl_stmt|;
DECL|method|ErasureCoderOptions (int numDataUnits, int numParityUnits)
specifier|public
name|ErasureCoderOptions
parameter_list|(
name|int
name|numDataUnits
parameter_list|,
name|int
name|numParityUnits
parameter_list|)
block|{
name|this
argument_list|(
name|numDataUnits
argument_list|,
name|numParityUnits
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|ErasureCoderOptions (int numDataUnits, int numParityUnits, boolean allowChangeInputs, boolean allowVerboseDump)
specifier|public
name|ErasureCoderOptions
parameter_list|(
name|int
name|numDataUnits
parameter_list|,
name|int
name|numParityUnits
parameter_list|,
name|boolean
name|allowChangeInputs
parameter_list|,
name|boolean
name|allowVerboseDump
parameter_list|)
block|{
name|this
operator|.
name|numDataUnits
operator|=
name|numDataUnits
expr_stmt|;
name|this
operator|.
name|numParityUnits
operator|=
name|numParityUnits
expr_stmt|;
name|this
operator|.
name|numAllUnits
operator|=
name|numDataUnits
operator|+
name|numParityUnits
expr_stmt|;
name|this
operator|.
name|allowChangeInputs
operator|=
name|allowChangeInputs
expr_stmt|;
name|this
operator|.
name|allowVerboseDump
operator|=
name|allowVerboseDump
expr_stmt|;
block|}
comment|/**    * The number of data input units for the coding. A unit can be a byte,    * chunk or buffer or even a block.    * @return count of data input units    */
DECL|method|getNumDataUnits ()
specifier|public
name|int
name|getNumDataUnits
parameter_list|()
block|{
return|return
name|numDataUnits
return|;
block|}
comment|/**    * The number of parity output units for the coding. A unit can be a byte,    * chunk, buffer or even a block.    * @return count of parity output units    */
DECL|method|getNumParityUnits ()
specifier|public
name|int
name|getNumParityUnits
parameter_list|()
block|{
return|return
name|numParityUnits
return|;
block|}
comment|/**    * The number of all the involved units in the coding.    * @return count of all the data units and parity units    */
DECL|method|getNumAllUnits ()
specifier|public
name|int
name|getNumAllUnits
parameter_list|()
block|{
return|return
name|numAllUnits
return|;
block|}
comment|/**    * Allow changing input buffer content (not positions). Maybe better    * performance if not allowed.    * @return true if allowing input content to be changed, false otherwise    */
DECL|method|allowChangeInputs ()
specifier|public
name|boolean
name|allowChangeInputs
parameter_list|()
block|{
return|return
name|allowChangeInputs
return|;
block|}
comment|/**    * Allow dump verbose debug info or not.    * @return true if verbose debug info is desired, false otherwise    */
DECL|method|allowVerboseDump ()
specifier|public
name|boolean
name|allowVerboseDump
parameter_list|()
block|{
return|return
name|allowVerboseDump
return|;
block|}
block|}
end_class

end_unit

