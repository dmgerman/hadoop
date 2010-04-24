begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.permission
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|permission
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
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
name|conf
operator|.
name|Configuration
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
name|CommonConfigurationKeys
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
name|io
operator|.
name|Writable
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
name|io
operator|.
name|WritableFactories
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
name|io
operator|.
name|WritableFactory
import|;
end_import

begin_comment
comment|/**  * A class for file/directory permissions.  */
end_comment

begin_class
DECL|class|FsPermission
specifier|public
class|class
name|FsPermission
implements|implements
name|Writable
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|FsPermission
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|FACTORY
specifier|static
specifier|final
name|WritableFactory
name|FACTORY
init|=
operator|new
name|WritableFactory
argument_list|()
block|{
specifier|public
name|Writable
name|newInstance
parameter_list|()
block|{
return|return
operator|new
name|FsPermission
argument_list|()
return|;
block|}
block|}
decl_stmt|;
static|static
block|{
comment|// register a ctor
name|WritableFactories
operator|.
name|setFactory
argument_list|(
name|FsPermission
operator|.
name|class
argument_list|,
name|FACTORY
argument_list|)
expr_stmt|;
block|}
comment|/** Create an immutable {@link FsPermission} object. */
DECL|method|createImmutable (short permission)
specifier|public
specifier|static
name|FsPermission
name|createImmutable
parameter_list|(
name|short
name|permission
parameter_list|)
block|{
return|return
operator|new
name|FsPermission
argument_list|(
name|permission
argument_list|)
block|{
specifier|public
name|FsPermission
name|applyUMask
parameter_list|(
name|FsPermission
name|umask
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|void
name|readFields
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
return|;
block|}
comment|//POSIX permission style
DECL|field|useraction
specifier|private
name|FsAction
name|useraction
init|=
literal|null
decl_stmt|;
DECL|field|groupaction
specifier|private
name|FsAction
name|groupaction
init|=
literal|null
decl_stmt|;
DECL|field|otheraction
specifier|private
name|FsAction
name|otheraction
init|=
literal|null
decl_stmt|;
DECL|field|stickyBit
specifier|private
name|boolean
name|stickyBit
init|=
literal|false
decl_stmt|;
DECL|method|FsPermission ()
specifier|private
name|FsPermission
parameter_list|()
block|{}
comment|/**    * Construct by the given {@link FsAction}.    * @param u user action    * @param g group action    * @param o other action    */
DECL|method|FsPermission (FsAction u, FsAction g, FsAction o)
specifier|public
name|FsPermission
parameter_list|(
name|FsAction
name|u
parameter_list|,
name|FsAction
name|g
parameter_list|,
name|FsAction
name|o
parameter_list|)
block|{
name|this
argument_list|(
name|u
argument_list|,
name|g
argument_list|,
name|o
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|FsPermission (FsAction u, FsAction g, FsAction o, boolean sb)
specifier|public
name|FsPermission
parameter_list|(
name|FsAction
name|u
parameter_list|,
name|FsAction
name|g
parameter_list|,
name|FsAction
name|o
parameter_list|,
name|boolean
name|sb
parameter_list|)
block|{
name|set
argument_list|(
name|u
argument_list|,
name|g
argument_list|,
name|o
argument_list|,
name|sb
argument_list|)
expr_stmt|;
block|}
comment|/**    * Construct by the given mode.    * @param mode    * @see #toShort()    */
DECL|method|FsPermission (short mode)
specifier|public
name|FsPermission
parameter_list|(
name|short
name|mode
parameter_list|)
block|{
name|fromShort
argument_list|(
name|mode
argument_list|)
expr_stmt|;
block|}
comment|/**    * Copy constructor    *     * @param other other permission    */
DECL|method|FsPermission (FsPermission other)
specifier|public
name|FsPermission
parameter_list|(
name|FsPermission
name|other
parameter_list|)
block|{
name|this
operator|.
name|useraction
operator|=
name|other
operator|.
name|useraction
expr_stmt|;
name|this
operator|.
name|groupaction
operator|=
name|other
operator|.
name|groupaction
expr_stmt|;
name|this
operator|.
name|otheraction
operator|=
name|other
operator|.
name|otheraction
expr_stmt|;
block|}
comment|/**    * Construct by given mode, either in octal or symbolic format.    * @param mode mode as a string, either in octal or symbolic format    * @throws IllegalArgumentException if<code>mode</code> is invalid    */
DECL|method|FsPermission (String mode)
specifier|public
name|FsPermission
parameter_list|(
name|String
name|mode
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|UmaskParser
argument_list|(
name|mode
argument_list|)
operator|.
name|getUMask
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** Return user {@link FsAction}. */
DECL|method|getUserAction ()
specifier|public
name|FsAction
name|getUserAction
parameter_list|()
block|{
return|return
name|useraction
return|;
block|}
comment|/** Return group {@link FsAction}. */
DECL|method|getGroupAction ()
specifier|public
name|FsAction
name|getGroupAction
parameter_list|()
block|{
return|return
name|groupaction
return|;
block|}
comment|/** Return other {@link FsAction}. */
DECL|method|getOtherAction ()
specifier|public
name|FsAction
name|getOtherAction
parameter_list|()
block|{
return|return
name|otheraction
return|;
block|}
DECL|method|set (FsAction u, FsAction g, FsAction o, boolean sb)
specifier|private
name|void
name|set
parameter_list|(
name|FsAction
name|u
parameter_list|,
name|FsAction
name|g
parameter_list|,
name|FsAction
name|o
parameter_list|,
name|boolean
name|sb
parameter_list|)
block|{
name|useraction
operator|=
name|u
expr_stmt|;
name|groupaction
operator|=
name|g
expr_stmt|;
name|otheraction
operator|=
name|o
expr_stmt|;
name|stickyBit
operator|=
name|sb
expr_stmt|;
block|}
DECL|method|fromShort (short n)
specifier|public
name|void
name|fromShort
parameter_list|(
name|short
name|n
parameter_list|)
block|{
name|FsAction
index|[]
name|v
init|=
name|FsAction
operator|.
name|values
argument_list|()
decl_stmt|;
name|set
argument_list|(
name|v
index|[
operator|(
name|n
operator|>>>
literal|6
operator|)
operator|&
literal|7
index|]
argument_list|,
name|v
index|[
operator|(
name|n
operator|>>>
literal|3
operator|)
operator|&
literal|7
index|]
argument_list|,
name|v
index|[
name|n
operator|&
literal|7
index|]
argument_list|,
operator|(
operator|(
operator|(
name|n
operator|>>>
literal|9
operator|)
operator|&
literal|1
operator|)
operator|==
literal|1
operator|)
argument_list|)
expr_stmt|;
block|}
comment|/** {@inheritDoc} */
DECL|method|write (DataOutput out)
specifier|public
name|void
name|write
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeShort
argument_list|(
name|toShort
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** {@inheritDoc} */
DECL|method|readFields (DataInput in)
specifier|public
name|void
name|readFields
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|fromShort
argument_list|(
name|in
operator|.
name|readShort
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create and initialize a {@link FsPermission} from {@link DataInput}.    */
DECL|method|read (DataInput in)
specifier|public
specifier|static
name|FsPermission
name|read
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|FsPermission
name|p
init|=
operator|new
name|FsPermission
argument_list|()
decl_stmt|;
name|p
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|p
return|;
block|}
comment|/**    * Encode the object to a short.    */
DECL|method|toShort ()
specifier|public
name|short
name|toShort
parameter_list|()
block|{
name|int
name|s
init|=
operator|(
name|stickyBit
condition|?
literal|1
operator|<<
literal|9
else|:
literal|0
operator|)
operator||
operator|(
name|useraction
operator|.
name|ordinal
argument_list|()
operator|<<
literal|6
operator|)
operator||
operator|(
name|groupaction
operator|.
name|ordinal
argument_list|()
operator|<<
literal|3
operator|)
operator||
name|otheraction
operator|.
name|ordinal
argument_list|()
decl_stmt|;
return|return
operator|(
name|short
operator|)
name|s
return|;
block|}
comment|/** {@inheritDoc} */
DECL|method|equals (Object obj)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|instanceof
name|FsPermission
condition|)
block|{
name|FsPermission
name|that
init|=
operator|(
name|FsPermission
operator|)
name|obj
decl_stmt|;
return|return
name|this
operator|.
name|useraction
operator|==
name|that
operator|.
name|useraction
operator|&&
name|this
operator|.
name|groupaction
operator|==
name|that
operator|.
name|groupaction
operator|&&
name|this
operator|.
name|otheraction
operator|==
name|that
operator|.
name|otheraction
operator|&&
name|this
operator|.
name|stickyBit
operator|==
name|that
operator|.
name|stickyBit
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/** {@inheritDoc} */
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|toShort
argument_list|()
return|;
block|}
comment|/** {@inheritDoc} */
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|String
name|str
init|=
name|useraction
operator|.
name|SYMBOL
operator|+
name|groupaction
operator|.
name|SYMBOL
operator|+
name|otheraction
operator|.
name|SYMBOL
decl_stmt|;
if|if
condition|(
name|stickyBit
condition|)
block|{
name|StringBuilder
name|str2
init|=
operator|new
name|StringBuilder
argument_list|(
name|str
argument_list|)
decl_stmt|;
name|str2
operator|.
name|replace
argument_list|(
name|str2
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|,
name|str2
operator|.
name|length
argument_list|()
argument_list|,
name|otheraction
operator|.
name|implies
argument_list|(
name|FsAction
operator|.
name|EXECUTE
argument_list|)
condition|?
literal|"t"
else|:
literal|"T"
argument_list|)
expr_stmt|;
name|str
operator|=
name|str2
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
return|return
name|str
return|;
block|}
comment|/** Apply a umask to this permission and return a new one */
DECL|method|applyUMask (FsPermission umask)
specifier|public
name|FsPermission
name|applyUMask
parameter_list|(
name|FsPermission
name|umask
parameter_list|)
block|{
return|return
operator|new
name|FsPermission
argument_list|(
name|useraction
operator|.
name|and
argument_list|(
name|umask
operator|.
name|useraction
operator|.
name|not
argument_list|()
argument_list|)
argument_list|,
name|groupaction
operator|.
name|and
argument_list|(
name|umask
operator|.
name|groupaction
operator|.
name|not
argument_list|()
argument_list|)
argument_list|,
name|otheraction
operator|.
name|and
argument_list|(
name|umask
operator|.
name|otheraction
operator|.
name|not
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
comment|/** umask property label deprecated key and code in getUMask method    *  to accommodate it may be removed in version .23 */
DECL|field|DEPRECATED_UMASK_LABEL
specifier|public
specifier|static
specifier|final
name|String
name|DEPRECATED_UMASK_LABEL
init|=
literal|"dfs.umask"
decl_stmt|;
DECL|field|UMASK_LABEL
specifier|public
specifier|static
specifier|final
name|String
name|UMASK_LABEL
init|=
name|CommonConfigurationKeys
operator|.
name|FS_PERMISSIONS_UMASK_KEY
decl_stmt|;
DECL|field|DEFAULT_UMASK
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_UMASK
init|=
name|CommonConfigurationKeys
operator|.
name|FS_PERMISSIONS_UMASK_DEFAULT
decl_stmt|;
comment|/**     * Get the user file creation mask (umask)    *     * {@code UMASK_LABEL} config param has umask value that is either symbolic     * or octal.    *     * Symbolic umask is applied relative to file mode creation mask;     * the permission op characters '+' clears the corresponding bit in the mask,     * '-' sets bits in the mask.    *     * Octal umask, the specified bits are set in the file mode creation mask.    *     * {@code DEPRECATED_UMASK_LABEL} config param has umask value set to decimal.    */
DECL|method|getUMask (Configuration conf)
specifier|public
specifier|static
name|FsPermission
name|getUMask
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|int
name|umask
init|=
name|DEFAULT_UMASK
decl_stmt|;
comment|// To ensure backward compatibility first use the deprecated key.
comment|// If the deprecated key is not present then check for the new key
if|if
condition|(
name|conf
operator|!=
literal|null
condition|)
block|{
name|String
name|confUmask
init|=
name|conf
operator|.
name|get
argument_list|(
name|UMASK_LABEL
argument_list|)
decl_stmt|;
name|int
name|oldUmask
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|DEPRECATED_UMASK_LABEL
argument_list|,
name|Integer
operator|.
name|MIN_VALUE
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|confUmask
operator|!=
literal|null
condition|)
block|{
name|umask
operator|=
operator|new
name|UmaskParser
argument_list|(
name|confUmask
argument_list|)
operator|.
name|getUMask
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
comment|// Provide more explanation for user-facing message
name|String
name|type
init|=
name|iae
operator|instanceof
name|NumberFormatException
condition|?
literal|"decimal"
else|:
literal|"octal or symbolic"
decl_stmt|;
name|String
name|error
init|=
literal|"Unable to parse configuration "
operator|+
name|UMASK_LABEL
operator|+
literal|" with value "
operator|+
name|confUmask
operator|+
literal|" as "
operator|+
name|type
operator|+
literal|" umask."
decl_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
name|error
argument_list|)
expr_stmt|;
comment|// If oldUmask is not set, then throw the exception
if|if
condition|(
name|oldUmask
operator|==
name|Integer
operator|.
name|MIN_VALUE
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|error
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|oldUmask
operator|!=
name|Integer
operator|.
name|MIN_VALUE
condition|)
block|{
comment|// Property was set with old key
if|if
condition|(
name|umask
operator|!=
name|oldUmask
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|DEPRECATED_UMASK_LABEL
operator|+
literal|" configuration key is deprecated. "
operator|+
literal|"Convert to "
operator|+
name|UMASK_LABEL
operator|+
literal|", using octal or symbolic umask "
operator|+
literal|"specifications."
argument_list|)
expr_stmt|;
comment|// Old and new umask values do not match - Use old umask
name|umask
operator|=
name|oldUmask
expr_stmt|;
block|}
block|}
block|}
return|return
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
name|umask
argument_list|)
return|;
block|}
DECL|method|getStickyBit ()
specifier|public
name|boolean
name|getStickyBit
parameter_list|()
block|{
return|return
name|stickyBit
return|;
block|}
comment|/** Set the user file creation mask (umask) */
DECL|method|setUMask (Configuration conf, FsPermission umask)
specifier|public
specifier|static
name|void
name|setUMask
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|FsPermission
name|umask
parameter_list|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|UMASK_LABEL
argument_list|,
name|String
operator|.
name|format
argument_list|(
literal|"%1$03o"
argument_list|,
name|umask
operator|.
name|toShort
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DEPRECATED_UMASK_LABEL
argument_list|,
name|umask
operator|.
name|toShort
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** Get the default permission. */
DECL|method|getDefault ()
specifier|public
specifier|static
name|FsPermission
name|getDefault
parameter_list|()
block|{
return|return
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|00777
argument_list|)
return|;
block|}
comment|/**    * Create a FsPermission from a Unix symbolic permission string    * @param unixSymbolicPermission e.g. "-rw-rw-rw-"    */
DECL|method|valueOf (String unixSymbolicPermission)
specifier|public
specifier|static
name|FsPermission
name|valueOf
parameter_list|(
name|String
name|unixSymbolicPermission
parameter_list|)
block|{
if|if
condition|(
name|unixSymbolicPermission
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|unixSymbolicPermission
operator|.
name|length
argument_list|()
operator|!=
literal|10
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"length != 10(unixSymbolicPermission="
operator|+
name|unixSymbolicPermission
operator|+
literal|")"
argument_list|)
throw|;
block|}
name|int
name|n
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|unixSymbolicPermission
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|n
operator|=
name|n
operator|<<
literal|1
expr_stmt|;
name|char
name|c
init|=
name|unixSymbolicPermission
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|n
operator|+=
operator|(
name|c
operator|==
literal|'-'
operator|||
name|c
operator|==
literal|'T'
operator|||
name|c
operator|==
literal|'S'
operator|)
condition|?
literal|0
else|:
literal|1
expr_stmt|;
block|}
comment|// Add sticky bit value if set
if|if
condition|(
name|unixSymbolicPermission
operator|.
name|charAt
argument_list|(
literal|9
argument_list|)
operator|==
literal|'t'
operator|||
name|unixSymbolicPermission
operator|.
name|charAt
argument_list|(
literal|9
argument_list|)
operator|==
literal|'T'
condition|)
name|n
operator|+=
literal|01000
expr_stmt|;
return|return
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
name|n
argument_list|)
return|;
block|}
block|}
end_class

end_unit

