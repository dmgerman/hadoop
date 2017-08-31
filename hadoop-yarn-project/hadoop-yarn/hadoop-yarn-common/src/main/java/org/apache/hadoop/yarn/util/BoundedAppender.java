begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|util
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
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

begin_comment
comment|/**  * A {@link CharSequence} appender that considers its {@link #limit} as upper  * bound.  *<p>  * When {@link #limit} would be reached on append, past messages will be  * truncated from head, and a header telling the user about truncation will be  * prepended, with ellipses in between header and messages.  *<p>  * Note that header and ellipses are not counted against {@link #limit}.  *<p>  * An example:  *  *<pre>  * {@code  *   // At the beginning it's an empty string  *   final Appendable shortAppender = new BoundedAppender(80);  *   // The whole message fits into limit  *   shortAppender.append(  *       "message1 this is a very long message but fitting into limit\n");  *   // The first message is truncated, the second not  *   shortAppender.append("message2 this is shorter than the previous one\n");  *   // The first message is deleted, the second truncated, the third  *   // preserved  *   shortAppender.append("message3 this is even shorter message, maybe.\n");  *   // The first two are deleted, the third one truncated, the last preserved  *   shortAppender.append("message4 the shortest one, yet the greatest :)");  *   // Current contents are like this:  *   // Diagnostic messages truncated, showing last 80 chars out of 199:  *   // ...s is even shorter message, maybe.  *   // message4 the shortest one, yet the greatest :)  * }  *</pre>  *<p>  * Note that<tt>null</tt> values are {@link #append(CharSequence) append}ed  * just like in {@link StringBuilder#append(CharSequence) original  * implementation}.  *<p>  * Note that this class is not thread safe.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Unstable
annotation|@
name|VisibleForTesting
DECL|class|BoundedAppender
specifier|public
class|class
name|BoundedAppender
block|{
annotation|@
name|VisibleForTesting
DECL|field|TRUNCATED_MESSAGES_TEMPLATE
specifier|public
specifier|static
specifier|final
name|String
name|TRUNCATED_MESSAGES_TEMPLATE
init|=
literal|"Diagnostic messages truncated, showing last "
operator|+
literal|"%d chars out of %d:%n...%s"
decl_stmt|;
DECL|field|limit
specifier|private
specifier|final
name|int
name|limit
decl_stmt|;
DECL|field|messages
specifier|private
specifier|final
name|StringBuilder
name|messages
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
DECL|field|totalCharacterCount
specifier|private
name|int
name|totalCharacterCount
init|=
literal|0
decl_stmt|;
DECL|method|BoundedAppender (final int limit)
specifier|public
name|BoundedAppender
parameter_list|(
specifier|final
name|int
name|limit
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|limit
operator|>
literal|0
argument_list|,
literal|"limit should be positive"
argument_list|)
expr_stmt|;
name|this
operator|.
name|limit
operator|=
name|limit
expr_stmt|;
block|}
comment|/**    * Append a {@link CharSequence} considering {@link #limit}, truncating    * from the head of {@code csq} or {@link #messages} when necessary.    *    * @param csq the {@link CharSequence} to append    * @return this    */
DECL|method|append (final CharSequence csq)
specifier|public
name|BoundedAppender
name|append
parameter_list|(
specifier|final
name|CharSequence
name|csq
parameter_list|)
block|{
name|appendAndCount
argument_list|(
name|csq
argument_list|)
expr_stmt|;
name|checkAndCut
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|appendAndCount (final CharSequence csq)
specifier|private
name|void
name|appendAndCount
parameter_list|(
specifier|final
name|CharSequence
name|csq
parameter_list|)
block|{
specifier|final
name|int
name|before
init|=
name|messages
operator|.
name|length
argument_list|()
decl_stmt|;
name|messages
operator|.
name|append
argument_list|(
name|csq
argument_list|)
expr_stmt|;
specifier|final
name|int
name|after
init|=
name|messages
operator|.
name|length
argument_list|()
decl_stmt|;
name|totalCharacterCount
operator|+=
name|after
operator|-
name|before
expr_stmt|;
block|}
DECL|method|checkAndCut ()
specifier|private
name|void
name|checkAndCut
parameter_list|()
block|{
if|if
condition|(
name|messages
operator|.
name|length
argument_list|()
operator|>
name|limit
condition|)
block|{
specifier|final
name|int
name|newStart
init|=
name|messages
operator|.
name|length
argument_list|()
operator|-
name|limit
decl_stmt|;
name|messages
operator|.
name|delete
argument_list|(
literal|0
argument_list|,
name|newStart
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Get current length of messages considering truncates    * without header and ellipses.    *    * @return current length    */
DECL|method|length ()
specifier|public
name|int
name|length
parameter_list|()
block|{
return|return
name|messages
operator|.
name|length
argument_list|()
return|;
block|}
DECL|method|getLimit ()
specifier|public
name|int
name|getLimit
parameter_list|()
block|{
return|return
name|limit
return|;
block|}
comment|/**    * Get a string representation of the actual contents, displaying also a    * header and ellipses when there was a truncate.    *    * @return String representation of the {@link #messages}    */
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
if|if
condition|(
name|messages
operator|.
name|length
argument_list|()
operator|<
name|totalCharacterCount
condition|)
block|{
return|return
name|String
operator|.
name|format
argument_list|(
name|TRUNCATED_MESSAGES_TEMPLATE
argument_list|,
name|messages
operator|.
name|length
argument_list|()
argument_list|,
name|totalCharacterCount
argument_list|,
name|messages
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
return|return
name|messages
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

