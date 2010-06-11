begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
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
name|fs
operator|.
name|permission
operator|.
name|FsPermission
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
name|util
operator|.
name|Progressable
import|;
end_import

begin_comment
comment|/**  * This class contains options related to file system operations.  */
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
DECL|class|Options
specifier|public
specifier|final
class|class
name|Options
block|{
comment|/**    * Class to support the varargs for create() options.    *    */
DECL|class|CreateOpts
specifier|public
specifier|static
class|class
name|CreateOpts
block|{
DECL|method|CreateOpts ()
specifier|private
name|CreateOpts
parameter_list|()
block|{ }
empty_stmt|;
DECL|method|blockSize (long bs)
specifier|public
specifier|static
name|BlockSize
name|blockSize
parameter_list|(
name|long
name|bs
parameter_list|)
block|{
return|return
operator|new
name|BlockSize
argument_list|(
name|bs
argument_list|)
return|;
block|}
DECL|method|bufferSize (int bs)
specifier|public
specifier|static
name|BufferSize
name|bufferSize
parameter_list|(
name|int
name|bs
parameter_list|)
block|{
return|return
operator|new
name|BufferSize
argument_list|(
name|bs
argument_list|)
return|;
block|}
DECL|method|repFac (short rf)
specifier|public
specifier|static
name|ReplicationFactor
name|repFac
parameter_list|(
name|short
name|rf
parameter_list|)
block|{
return|return
operator|new
name|ReplicationFactor
argument_list|(
name|rf
argument_list|)
return|;
block|}
DECL|method|bytesPerChecksum (short crc)
specifier|public
specifier|static
name|BytesPerChecksum
name|bytesPerChecksum
parameter_list|(
name|short
name|crc
parameter_list|)
block|{
return|return
operator|new
name|BytesPerChecksum
argument_list|(
name|crc
argument_list|)
return|;
block|}
DECL|method|perms (FsPermission perm)
specifier|public
specifier|static
name|Perms
name|perms
parameter_list|(
name|FsPermission
name|perm
parameter_list|)
block|{
return|return
operator|new
name|Perms
argument_list|(
name|perm
argument_list|)
return|;
block|}
DECL|method|createParent ()
specifier|public
specifier|static
name|CreateParent
name|createParent
parameter_list|()
block|{
return|return
operator|new
name|CreateParent
argument_list|(
literal|true
argument_list|)
return|;
block|}
DECL|method|donotCreateParent ()
specifier|public
specifier|static
name|CreateParent
name|donotCreateParent
parameter_list|()
block|{
return|return
operator|new
name|CreateParent
argument_list|(
literal|false
argument_list|)
return|;
block|}
DECL|class|BlockSize
specifier|public
specifier|static
class|class
name|BlockSize
extends|extends
name|CreateOpts
block|{
DECL|field|blockSize
specifier|private
specifier|final
name|long
name|blockSize
decl_stmt|;
DECL|method|BlockSize (long bs)
specifier|protected
name|BlockSize
parameter_list|(
name|long
name|bs
parameter_list|)
block|{
if|if
condition|(
name|bs
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Block size must be greater than 0"
argument_list|)
throw|;
block|}
name|blockSize
operator|=
name|bs
expr_stmt|;
block|}
DECL|method|getValue ()
specifier|public
name|long
name|getValue
parameter_list|()
block|{
return|return
name|blockSize
return|;
block|}
block|}
DECL|class|ReplicationFactor
specifier|public
specifier|static
class|class
name|ReplicationFactor
extends|extends
name|CreateOpts
block|{
DECL|field|replication
specifier|private
specifier|final
name|short
name|replication
decl_stmt|;
DECL|method|ReplicationFactor (short rf)
specifier|protected
name|ReplicationFactor
parameter_list|(
name|short
name|rf
parameter_list|)
block|{
if|if
condition|(
name|rf
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Replication must be greater than 0"
argument_list|)
throw|;
block|}
name|replication
operator|=
name|rf
expr_stmt|;
block|}
DECL|method|getValue ()
specifier|public
name|short
name|getValue
parameter_list|()
block|{
return|return
name|replication
return|;
block|}
block|}
DECL|class|BufferSize
specifier|public
specifier|static
class|class
name|BufferSize
extends|extends
name|CreateOpts
block|{
DECL|field|bufferSize
specifier|private
specifier|final
name|int
name|bufferSize
decl_stmt|;
DECL|method|BufferSize (int bs)
specifier|protected
name|BufferSize
parameter_list|(
name|int
name|bs
parameter_list|)
block|{
if|if
condition|(
name|bs
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Buffer size must be greater than 0"
argument_list|)
throw|;
block|}
name|bufferSize
operator|=
name|bs
expr_stmt|;
block|}
DECL|method|getValue ()
specifier|public
name|int
name|getValue
parameter_list|()
block|{
return|return
name|bufferSize
return|;
block|}
block|}
DECL|class|BytesPerChecksum
specifier|public
specifier|static
class|class
name|BytesPerChecksum
extends|extends
name|CreateOpts
block|{
DECL|field|bytesPerChecksum
specifier|private
specifier|final
name|int
name|bytesPerChecksum
decl_stmt|;
DECL|method|BytesPerChecksum (short bpc)
specifier|protected
name|BytesPerChecksum
parameter_list|(
name|short
name|bpc
parameter_list|)
block|{
if|if
condition|(
name|bpc
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Bytes per checksum must be greater than 0"
argument_list|)
throw|;
block|}
name|bytesPerChecksum
operator|=
name|bpc
expr_stmt|;
block|}
DECL|method|getValue ()
specifier|public
name|int
name|getValue
parameter_list|()
block|{
return|return
name|bytesPerChecksum
return|;
block|}
block|}
DECL|class|Perms
specifier|public
specifier|static
class|class
name|Perms
extends|extends
name|CreateOpts
block|{
DECL|field|permissions
specifier|private
specifier|final
name|FsPermission
name|permissions
decl_stmt|;
DECL|method|Perms (FsPermission perm)
specifier|protected
name|Perms
parameter_list|(
name|FsPermission
name|perm
parameter_list|)
block|{
if|if
condition|(
name|perm
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Permissions must not be null"
argument_list|)
throw|;
block|}
name|permissions
operator|=
name|perm
expr_stmt|;
block|}
DECL|method|getValue ()
specifier|public
name|FsPermission
name|getValue
parameter_list|()
block|{
return|return
name|permissions
return|;
block|}
block|}
DECL|class|Progress
specifier|public
specifier|static
class|class
name|Progress
extends|extends
name|CreateOpts
block|{
DECL|field|progress
specifier|private
specifier|final
name|Progressable
name|progress
decl_stmt|;
DECL|method|Progress (Progressable prog)
specifier|protected
name|Progress
parameter_list|(
name|Progressable
name|prog
parameter_list|)
block|{
if|if
condition|(
name|prog
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Progress must not be null"
argument_list|)
throw|;
block|}
name|progress
operator|=
name|prog
expr_stmt|;
block|}
DECL|method|getValue ()
specifier|public
name|Progressable
name|getValue
parameter_list|()
block|{
return|return
name|progress
return|;
block|}
block|}
DECL|class|CreateParent
specifier|public
specifier|static
class|class
name|CreateParent
extends|extends
name|CreateOpts
block|{
DECL|field|createParent
specifier|private
specifier|final
name|boolean
name|createParent
decl_stmt|;
DECL|method|CreateParent (boolean createPar)
specifier|protected
name|CreateParent
parameter_list|(
name|boolean
name|createPar
parameter_list|)
block|{
name|createParent
operator|=
name|createPar
expr_stmt|;
block|}
DECL|method|getValue ()
specifier|public
name|boolean
name|getValue
parameter_list|()
block|{
return|return
name|createParent
return|;
block|}
block|}
comment|/**      * Get an option of desired type      * @param theClass is the desired class of the opt      * @param opts - not null - at least one opt must be passed      * @return an opt from one of the opts of type theClass.      *   returns null if there isn't any      */
DECL|method|getOpt (Class<? extends CreateOpts> theClass, CreateOpts ...opts)
specifier|protected
specifier|static
name|CreateOpts
name|getOpt
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|CreateOpts
argument_list|>
name|theClass
parameter_list|,
name|CreateOpts
modifier|...
name|opts
parameter_list|)
block|{
if|if
condition|(
name|opts
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Null opt"
argument_list|)
throw|;
block|}
name|CreateOpts
name|result
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|opts
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|opts
index|[
name|i
index|]
operator|.
name|getClass
argument_list|()
operator|==
name|theClass
condition|)
block|{
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"multiple blocksize varargs"
argument_list|)
throw|;
name|result
operator|=
name|opts
index|[
name|i
index|]
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
comment|/**      * set an option      * @param newValue  the option to be set      * @param opts  - the option is set into this array of opts      * @return updated CreateOpts[] == opts + newValue      */
DECL|method|setOpt (T newValue, CreateOpts ...opts)
specifier|protected
specifier|static
parameter_list|<
name|T
extends|extends
name|CreateOpts
parameter_list|>
name|CreateOpts
index|[]
name|setOpt
parameter_list|(
name|T
name|newValue
parameter_list|,
name|CreateOpts
modifier|...
name|opts
parameter_list|)
block|{
name|boolean
name|alreadyInOpts
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|opts
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|opts
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|opts
index|[
name|i
index|]
operator|.
name|getClass
argument_list|()
operator|==
name|newValue
operator|.
name|getClass
argument_list|()
condition|)
block|{
if|if
condition|(
name|alreadyInOpts
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"multiple opts varargs"
argument_list|)
throw|;
name|alreadyInOpts
operator|=
literal|true
expr_stmt|;
name|opts
index|[
name|i
index|]
operator|=
name|newValue
expr_stmt|;
block|}
block|}
block|}
name|CreateOpts
index|[]
name|resultOpt
init|=
name|opts
decl_stmt|;
if|if
condition|(
operator|!
name|alreadyInOpts
condition|)
block|{
comment|// no newValue in opt
name|CreateOpts
index|[]
name|newOpts
init|=
operator|new
name|CreateOpts
index|[
name|opts
operator|.
name|length
operator|+
literal|1
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|opts
argument_list|,
literal|0
argument_list|,
name|newOpts
argument_list|,
literal|0
argument_list|,
name|opts
operator|.
name|length
argument_list|)
expr_stmt|;
name|newOpts
index|[
name|opts
operator|.
name|length
index|]
operator|=
name|newValue
expr_stmt|;
name|resultOpt
operator|=
name|newOpts
expr_stmt|;
block|}
return|return
name|resultOpt
return|;
block|}
block|}
comment|/**    * Enum to support the varargs for rename() options    */
DECL|enum|Rename
specifier|public
specifier|static
enum|enum
name|Rename
block|{
DECL|enumConstant|NONE
name|NONE
argument_list|(
operator|(
name|byte
operator|)
literal|0
argument_list|)
block|,
comment|// No options
DECL|enumConstant|OVERWRITE
name|OVERWRITE
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|)
block|;
comment|// Overwrite the rename destination
DECL|field|code
specifier|private
specifier|final
name|byte
name|code
decl_stmt|;
DECL|method|Rename (byte code)
specifier|private
name|Rename
parameter_list|(
name|byte
name|code
parameter_list|)
block|{
name|this
operator|.
name|code
operator|=
name|code
expr_stmt|;
block|}
DECL|method|valueOf (byte code)
specifier|public
specifier|static
name|Rename
name|valueOf
parameter_list|(
name|byte
name|code
parameter_list|)
block|{
return|return
name|code
operator|<
literal|0
operator|||
name|code
operator|>=
name|values
argument_list|()
operator|.
name|length
condition|?
literal|null
else|:
name|values
argument_list|()
index|[
name|code
index|]
return|;
block|}
DECL|method|value ()
specifier|public
name|byte
name|value
parameter_list|()
block|{
return|return
name|code
return|;
block|}
block|}
block|}
end_class

end_unit

