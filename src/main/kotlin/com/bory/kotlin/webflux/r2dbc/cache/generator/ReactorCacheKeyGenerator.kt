package com.bory.kotlin.webflux.r2dbc.cache.generator

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.beans.factory.BeanFactory
import org.springframework.context.expression.BeanFactoryResolver
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext
import org.springframework.stereotype.Component

@Component
class ReactorCacheKeyGenerator(
    private val beanFactory: BeanFactory
) {
  fun generateKey(joinPoint: ProceedingJoinPoint, key: String): String {
    val args = joinPoint.args.toList()
    val evaluatedKey = evaluateSpelKey(joinPoint, key)

    return (if (evaluatedKey == null || evaluatedKey.isEmpty()) {
      if (args.isEmpty()) "all" else args.joinToString(":") { it.toString() }
    } else evaluatedKey)
  }

  private fun evaluateSpelKey(joinPoint: ProceedingJoinPoint, key: String): String? =
      try {
        createStandardEvaluationContext(joinPoint)
            .let { context ->
              val expression = SpelExpressionParser().parseExpression(key)
              expression.getValue(context, String::class.java)
            }
      } catch (e: Exception) {
        key
      }


  private fun createStandardEvaluationContext(joinPoint: ProceedingJoinPoint) =
      StandardEvaluationContext().apply {
        setBeanResolver(BeanFactoryResolver(beanFactory))
        setVariable("args", joinPoint.args)

        (joinPoint.signature as MethodSignature).method.parameters.forEachIndexed { i, param ->
          setVariable(param.name, joinPoint.args[i])
        }
      }
}
